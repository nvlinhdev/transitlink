package vn.edu.fpt.transitlink.location.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.location.dto.ImportPlaceResultDTO;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.entity.Place;
import vn.edu.fpt.transitlink.location.entity.PlaceDocument;
import vn.edu.fpt.transitlink.location.mapper.PlaceMapper;
import vn.edu.fpt.transitlink.location.repository.PlaceESRepository;
import vn.edu.fpt.transitlink.location.repository.PlaceRepository;
import vn.edu.fpt.transitlink.location.request.ImportPlaceRequest;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.location.spi.PlaceSearchProvider;
import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceMapper mapper;
    private final PlaceRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final PlaceSearchProvider searchProvider;
    private final PlaceESRepository placeESRepository;

    @Override
    public PlaceDTO getPlace(UUID id) {
        Optional<Place> place = repository.findById(id);
        return place.map(mapper::toDTO).orElseThrow(() -> new RuntimeException("Place not found"));
    }

    @Override
    public PlaceDTO savePlace(PlaceDTO placeDTO) {
        Place place = mapper.toEntity(placeDTO);
        Place savedPlace = repository.save(place);
        indexPlaceToElasticsearch(savedPlace);
        return mapper.toDTO(savedPlace);
    }

    @Override
    @Transactional
    public ImportPlaceResultDTO importPlaces(List<ImportPlaceRequest> requests) {
        if (requests.isEmpty()) {
            return new ImportPlaceResultDTO(0, 0, List.of(), List.of());
        }

        List<PlaceDTO> results = new ArrayList<>(Collections.nCopies(requests.size(), null));
        List<ImportErrorDTO> errors = new ArrayList<>();

        try {
            // B1. Gom các request trùng nhau theo key lat,lon
            Map<String, List<Integer>> coordinateToIndexes = new HashMap<>();
            for (int i = 0; i < requests.size(); i++) {
                ImportPlaceRequest r = requests.get(i);
                String key = r.latitude() + "," + r.longitude();
                coordinateToIndexes.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            }

            // B2. Lấy danh sách unique tọa độ
            Set<String> uniqueCoordinates = coordinateToIndexes.keySet();

            // B3. Truy vấn DB lấy các place đã tồn tại
            String placeholders = String.join(",", Collections.nCopies(uniqueCoordinates.size(), "?"));
            String sql = "SELECT * FROM places WHERE CONCAT(latitude, ',', longitude) IN (" + placeholders + ")";
            List<Place> existingPlaces = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(Place.class),
                    uniqueCoordinates.toArray()
            );

            Map<String, Place> existingMap = existingPlaces.stream()
                    .collect(Collectors.toMap(
                            p -> p.getLatitude() + "," + p.getLongitude(),
                            p -> p
                    ));

            List<Place> newPlacesToSave = new ArrayList<>();
            Map<String, Place> newMap = new HashMap<>();

            // B4. Xử lý từng group theo coordinate
            for (var entry : coordinateToIndexes.entrySet()) {
                String coordKey = entry.getKey();
                List<Integer> indexes = entry.getValue();

                Place place;
                if (existingMap.containsKey(coordKey)) {
                    place = existingMap.get(coordKey);
                } else if (newMap.containsKey(coordKey)) {
                    place = newMap.get(coordKey);
                } else {
                    // Chưa có thì tạo entity mới
                    ImportPlaceRequest representativeReq = requests.get(indexes.get(0));
                    place = mapper.toEntity(representativeReq);
                    newPlacesToSave.add(place);
                    newMap.put(coordKey, place);
                }

                // Tạm thời map DTO null, lát nữa khi save xong sẽ update
            }

            // B5. Save tất cả new place
            if (!newPlacesToSave.isEmpty()) {
                List<Place> savedPlaces = repository.saveAll(newPlacesToSave);
                savedPlaces.forEach(p -> newMap.put(p.getLatitude() + "," + p.getLongitude(), p));
                indexPlacesToElasticsearch(savedPlaces);
            }

            // B6. Điền kết quả cho từng index trong results
            for (var entry : coordinateToIndexes.entrySet()) {
                String coordKey = entry.getKey();
                Place place = existingMap.containsKey(coordKey) ? existingMap.get(coordKey) : newMap.get(coordKey);
                PlaceDTO dto = mapper.toDTO(place);

                for (Integer idx : entry.getValue()) {
                    results.set(idx, dto);
                }
            }

        } catch (Exception ex) {
            for (int i = 0; i < requests.size(); i++) {
                errors.add(new ImportErrorDTO(i, "Batch processing failed: " + ex.getMessage()));
            }
            return new ImportPlaceResultDTO(0, requests.size(), errors, List.of());
        }

        int successful = (int) results.stream().filter(Objects::nonNull).count();
        int failed = requests.size() - successful;

        return new ImportPlaceResultDTO(successful, failed, errors, results);
    }

    private void indexPlacesToElasticsearch(List<Place> savedPlaces) {
        try {
            List<PlaceDocument> documentsToIndex = savedPlaces.stream()
                    .map(mapper::toDocument)
                    .toList();

            placeESRepository.saveAll(documentsToIndex);
            log.debug("Successfully indexed {} places to Elasticsearch", savedPlaces.size());

        } catch (Exception ex) {
            log.warn("Failed to bulk index places to Elasticsearch: {}", ex.getMessage());

            // Individual ES indexing fallback
            savedPlaces.forEach(place -> {
                try {
                    PlaceDocument document = mapper.toDocument(place);
                    placeESRepository.save(document);
                } catch (Exception innerEx) {
                    log.warn("Failed to index place {} to ES: {}", place.getId(), innerEx.getMessage());
                    // ES indexing errors không add vào main errors vì không critical
                }
            });
        }
    }

    private void handleIndividualPlaceSave(
            List<Place> newPlacesToSave,
            Map<Place, Integer> placeToIndexMap,
            List<PlaceDTO> results,
            List<ImportErrorDTO> errors) {

        List<Place> successfullyPersisted = new ArrayList<>();

        for (Place place : newPlacesToSave) {
            Integer requestIndex = placeToIndexMap.get(place);

            try {
                Place saved = repository.save(place);
                results.add(mapper.toDTO(saved));
                successfullyPersisted.add(saved);

            } catch (Exception innerEx) {
                errors.add(new ImportErrorDTO(requestIndex != null ? requestIndex : -1,
                        "Failed to save place: " + innerEx.getMessage()));
            }
        }

        // Index successful places to ES
        if (!successfullyPersisted.isEmpty()) {
            indexPlacesToElasticsearch(successfullyPersisted);
        }
    }

    @Override
    public List<PlaceDTO> search(String query) {
        List<PlaceDocument> places = placeESRepository.findByNameMatch(query);
        if (!places.isEmpty()) {
            return places.stream().map(mapper::toDTO).toList();
        }
        List<PlaceDTO> providerResults = searchProvider.searchPlaces(query);
        // Lưu các kết quả vào db nếu chưa tồn tại
        for (PlaceDTO dto : providerResults) {
            if (!repository.existsById(dto.id())) {
                repository.save(mapper.toEntity(dto));
                indexPlaceToElasticsearch(mapper.toEntity(dto));
            }
        }
        return providerResults;
    }

    private void indexPlaceToElasticsearch(Place place) {
        PlaceDocument doc = mapper.toDocument(place);
        placeESRepository.save(doc);
    }
}

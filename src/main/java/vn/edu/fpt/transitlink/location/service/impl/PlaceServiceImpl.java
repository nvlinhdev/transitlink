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

        List<PlaceDTO> results = new ArrayList<>();
        List<ImportErrorDTO> errors = new ArrayList<>();

        try {
            // Step 1: Bulk check existing places
            Map<String, Place> existingPlaceMap = bulkCheckExistingPlaces(requests);

            // Step 2: Separate existing vs new places
            List<Place> newPlacesToSave = new ArrayList<>();
            Map<Place, Integer> placeToIndexMap = new HashMap<>();

            for (int i = 0; i < requests.size(); i++) {
                ImportPlaceRequest request = requests.get(i);
                String coordinateKey = request.latitude() + "," + request.longitude();

                try {
                    Place existingPlace = existingPlaceMap.get(coordinateKey);

                    if (existingPlace != null) {
                        results.add(mapper.toDTO(existingPlace));
                    } else {
                        Place newPlace = mapper.toEntity(request);
                        newPlacesToSave.add(newPlace);
                        placeToIndexMap.put(newPlace, i);
                    }
                } catch (Exception ex) {
                    errors.add(new ImportErrorDTO(i, "Failed to process place: " + ex.getMessage()));
                }
            }

            // Step 3: Save new places và index ES
            if (!newPlacesToSave.isEmpty()) {
                savePlacesWithESIndexing(newPlacesToSave, placeToIndexMap, results, errors);
            }

        } catch (Exception ex) {
            for (int i = 0; i < requests.size(); i++) {
                errors.add(new ImportErrorDTO(i, "Batch processing failed: " + ex.getMessage()));
            }
            return new ImportPlaceResultDTO(0, requests.size(), errors, List.of());
        }

        int successful = results.size();
        int failed = requests.size() - successful;

        return new ImportPlaceResultDTO(successful, failed, errors, results);
    }

    private Map<String, Place> bulkCheckExistingPlaces(List<ImportPlaceRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uniqueCoordinates = requests.stream()
                .map(r -> r.latitude() + "," + r.longitude())
                .distinct()
                .toList();

        if (uniqueCoordinates.isEmpty()) {
            return Collections.emptyMap();
        }

        String placeholders = uniqueCoordinates.stream()
                .map(c -> "?")
                .collect(Collectors.joining(","));

        String sql = "SELECT * FROM places WHERE CONCAT(latitude, ',', longitude) IN (" + placeholders + ")";

        List<Place> existingPlaces = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Place.class),
                uniqueCoordinates.toArray());

        return existingPlaces.stream()
                .collect(Collectors.toMap(
                        place -> place.getLatitude() + "," + place.getLongitude(),
                        place -> place,
                        (existing, replacement) -> existing
                ));
    }

    private void savePlacesWithESIndexing(
            List<Place> newPlacesToSave,
            Map<Place, Integer> placeToIndexMap,
            List<PlaceDTO> results,
            List<ImportErrorDTO> errors) {

        try {
            // Bulk save to database
            List<Place> savedPlaces = repository.saveAll(newPlacesToSave);

            // Add to results
            savedPlaces.forEach(saved -> results.add(mapper.toDTO(saved)));

            // Sync ES indexing
            indexPlacesToElasticsearch(savedPlaces);

        } catch (Exception ex) {
            // Fallback: individual saves
            handleIndividualPlaceSave(newPlacesToSave, placeToIndexMap, results, errors);
        }
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

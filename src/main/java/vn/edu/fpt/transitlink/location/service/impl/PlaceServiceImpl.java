package vn.edu.fpt.transitlink.location.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.entity.Place;
import vn.edu.fpt.transitlink.location.entity.PlaceDocument;
import vn.edu.fpt.transitlink.location.mapper.PlaceMapper;
import vn.edu.fpt.transitlink.location.repository.PlaceESRepository;
import vn.edu.fpt.transitlink.location.repository.PlaceRepository;
import vn.edu.fpt.transitlink.location.request.ImportPlaceRequest;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.location.spi.PlaceSearchProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceMapper mapper;
    private final PlaceRepository repository;
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
    public List<PlaceDTO> importPlaces(List<ImportPlaceRequest> request) {
        if (request.isEmpty()) {
            return new ArrayList<>();
        }

        // Chuẩn bị danh sách tọa độ để bulk check
        List<Object[]> coordinates = request.stream()
                .map(req -> new Object[]{req.latitude(), req.longitude()})
                .toList();

        // Bulk check tất cả tọa độ có tồn tại không
        List<Place> existingPlaces = repository.findByCoordinates(coordinates);

        // Tạo map để lookup nhanh
        Map<String, Place> existingPlaceMap = existingPlaces.stream()
                .collect(Collectors.toMap(
                        place -> place.getLatitude() + "," + place.getLongitude(),
                        place -> place
                ));

        List<PlaceDTO> results = new ArrayList<>();
        List<Place> newPlacesToSave = new ArrayList<>();

        for (ImportPlaceRequest importRequest : request) {
            String coordinateKey = importRequest.latitude() + "," + importRequest.longitude();
            Place existingPlace = existingPlaceMap.get(coordinateKey);

            if (existingPlace != null) {
                // Nếu đã tồn tại, sử dụng place có sẵn
                results.add(mapper.toDTO(existingPlace));
            } else {
                // Nếu chưa tồn tại, chuẩn bị để bulk insert
                Place newPlace = mapper.toEntity(importRequest);
                newPlacesToSave.add(newPlace);
                results.add(mapper.toDTO(newPlace));
            }
        }

        // Bulk insert các place mới và bulk index vào Elasticsearch
        if (!newPlacesToSave.isEmpty()) {
            List<Place> savedPlaces = repository.saveAll(newPlacesToSave);

            // Bulk index vào Elasticsearch
            List<PlaceDocument> documentsToIndex = savedPlaces.stream()
                    .map(mapper::toDocument)
                    .toList();

            placeESRepository.saveAll(documentsToIndex);
        }

        return results;
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

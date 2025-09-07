package vn.edu.fpt.transitlink.location.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.entity.Place;
import vn.edu.fpt.transitlink.location.entity.PlaceDocument;
import vn.edu.fpt.transitlink.location.mapper.PlaceMapper;
import vn.edu.fpt.transitlink.location.repository.PlaceESRepository;
import vn.edu.fpt.transitlink.location.repository.PlaceRepository;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.location.spi.PlaceSearchProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

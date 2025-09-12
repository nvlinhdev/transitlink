package vn.edu.fpt.transitlink.location.service;

import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.request.ImportPlaceRequest;

import java.util.List;
import java.util.UUID;

public interface PlaceService {
    PlaceDTO getPlace(UUID id);
    PlaceDTO savePlace(PlaceDTO placeDTO);
    List<PlaceDTO> importPlaces(List<ImportPlaceRequest> request);
    List<PlaceDTO> search(String query);
}

package vn.edu.fpt.transitlink.location.spi;

import vn.edu.fpt.transitlink.location.dto.PlaceDTO;

import java.util.List;

public interface PlaceSearchProvider {
    List<PlaceDTO> searchPlaces(String query);
}

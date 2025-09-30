package vn.edu.fpt.transitlink.location.dto;

import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;

import java.util.List;

public record ImportPlaceResultDTO(
        int successful,
        int failed,
        List<ImportErrorDTO> errors,
        List<PlaceDTO> places
) {
}

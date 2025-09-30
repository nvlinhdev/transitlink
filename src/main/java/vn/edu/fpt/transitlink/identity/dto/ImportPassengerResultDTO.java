package vn.edu.fpt.transitlink.identity.dto;

import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;

import java.util.List;

public record ImportPassengerResultDTO(
        int successful,
        int failed,
        List<ImportErrorDTO> errors,
        List<PassengerDTO> passengers
) {}


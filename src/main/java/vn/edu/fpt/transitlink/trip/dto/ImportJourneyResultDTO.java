package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;

import java.util.List;

public record ImportJourneyResultDTO(
        int totalRows,
        int successCount,
        int errorCount,
        List<ImportErrorDTO> errors
) {
}

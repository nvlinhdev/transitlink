package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;

import java.util.List;

public record ImportJourneyResultDTO(
        Integer totalRows,
        Integer successCount,
        Integer errorCount,
        List<ImportErrorDTO> errors
) {
}

package vn.edu.fpt.transitlink.trip.dto;

import java.util.List;

public record ImportResultDTO(
        int totalRows,
        int successfulImports,
        int failedImports,
        List<PassengerJourneyDTO> successfulJourneys,
        List<ImportErrorDTO> errors
) {
    public record ImportErrorDTO(
            int rowNumber,
            String errorMessage,
            String rowData
    ) {
    }
}

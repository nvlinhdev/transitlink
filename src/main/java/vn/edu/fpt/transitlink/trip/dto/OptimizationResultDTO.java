package vn.edu.fpt.transitlink.trip.dto;

import java.util.List;

public record OptimizationResultDTO(
        int totalPassengers,
        int totalVehicles,
        int numberOfVehiclesUsed,
        int unservedPassengers,
        List<RouteSummaryDTO> routes,
        List<PassengerJourneyInfo> unservedPassengerJourneys
) {
}

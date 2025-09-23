package vn.edu.fpt.transitlink.trip.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class OptimizationResult {
    List<UUID> droppedPassengerJourneyIds;
    List<RouteData> routes;
    List<PassengerJourneyData> passengerJourneys;
}

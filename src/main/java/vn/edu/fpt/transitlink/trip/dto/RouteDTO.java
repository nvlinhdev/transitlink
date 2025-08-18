package vn.edu.fpt.transitlink.trip.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RouteDTO(
         UUID Id,
         UUID driverId,
         UUID vehicleId,
         String name,
         float estimatedDistanceKm,
         float estimatedDurationMin,
         OffsetDateTime plannedDepartureTime,
         OffsetDateTime  checkinTime,
         OffsetDateTime  plannedArrivalTime,
         OffsetDateTime checkoutTime,
         String status
) {
}

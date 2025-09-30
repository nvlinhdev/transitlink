package vn.edu.fpt.transitlink.trip.dto;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DriverRouteSummaryDTO(
        UUID id,
        Double estimatedDistanceKm,
        Double estimatedDurationMin,
        OffsetDateTime plannedDepartureTime,
        OffsetDateTime plannedArrivalTime,
        RouteStatus routeStatus,
        RouteType type
) {
}

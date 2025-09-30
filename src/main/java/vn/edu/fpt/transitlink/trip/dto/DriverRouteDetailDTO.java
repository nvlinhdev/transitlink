package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DriverRouteDetailDTO(
        UUID id,
        VehicleDTO vehicle,
        Double estimatedDistanceKm,
        Double estimatedDurationMin,
        OffsetDateTime plannedDepartureTime,
        OffsetDateTime plannedArrivalTime,
        String geometry,
        String directionUrl,
        RouteType type,
        RouteStatus status,
        List<StopDTO> stops
) {
}

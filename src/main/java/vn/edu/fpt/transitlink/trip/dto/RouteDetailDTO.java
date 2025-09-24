package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.identity.dto.DriverInfo;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RouteDetailDTO(
        UUID id,
        DriverInfo driver,
        VehicleDTO vehicleId,
        Double estimatedDistanceKm,
        Double estimatedDurationMin,
        OffsetDateTime plannedDepartureTime,
        OffsetDateTime checkinTime,
        OffsetDateTime plannedArrivalTime,
        OffsetDateTime checkoutTime,
        String geometry,
        RouteType type,
        RouteStatus status,
        List<StopDTO> stops
) {
}

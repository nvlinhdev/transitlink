package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.identity.dto.DriverInfo;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RouteSummaryDTO(
        UUID routeId,
        OffsetDateTime plannedDepartureTime,
        OffsetDateTime plannedArrivalTime,
        double distance,
        double duration,
        RouteStatus status,
        DriverInfo driverInfo,
        VehicleDTO vehicleDTO
) {
}

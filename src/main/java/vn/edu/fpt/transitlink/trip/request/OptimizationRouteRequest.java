package vn.edu.fpt.transitlink.trip.request;

import vn.edu.fpt.transitlink.trip.enumeration.RouteType;

import java.util.List;
import java.util.UUID;

public record OptimizationRouteRequest(
        List<UUID> passengerJourneyIds,
        RouteType routeType,
        List<UUID> vehicleIds
 ) {
}

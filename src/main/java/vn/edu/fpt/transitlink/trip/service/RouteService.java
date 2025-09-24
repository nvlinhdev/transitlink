package vn.edu.fpt.transitlink.trip.service;

import vn.edu.fpt.transitlink.trip.dto.*;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    OptimizationResultDTO optimizeRoute(OptimizationRouteRequest request);
    List<RouteSummaryDTO> getRoutes(int page, int size, UUID createdUserId);
    long countRoutes(UUID createdUserId);
    RouteDetailDTO getRoute(UUID routeId);
    void assignDriverToRoute(UUID driverId, UUID routeId);
    void publishRoute(UUID routeId);
    List<DriverRouteSummaryDTO> getAllDriverRouteByDriverId(UUID driverRouteId);
    DriverRouteDetailDTO getDriverRouteById(UUID routeId);

}

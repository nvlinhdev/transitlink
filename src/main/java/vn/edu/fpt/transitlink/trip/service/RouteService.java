package vn.edu.fpt.transitlink.trip.service;

import vn.edu.fpt.transitlink.trip.dto.RouteDTO;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;

import java.util.List;

public interface RouteService {
    List<RouteDTO> optimizeRoute(OptimizationRouteRequest request);

}

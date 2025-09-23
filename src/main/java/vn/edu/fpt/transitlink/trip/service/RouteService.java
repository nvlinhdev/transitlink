package vn.edu.fpt.transitlink.trip.service;

import vn.edu.fpt.transitlink.trip.dto.RouteDTO;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;

import java.util.List;
import java.util.UUID;

public interface RouteService {

    //    List<RouteDTO> testOptimize();
    List<RouteDTO> optimizeRoute(OptimizationRouteRequest request);
}

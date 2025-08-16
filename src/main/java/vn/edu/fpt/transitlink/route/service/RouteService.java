package vn.edu.fpt.transitlink.route.service;

import vn.edu.fpt.transitlink.route.dto.RouteDTO;

import java.util.UUID;

public interface RouteService {

    RouteDTO createRouteData(RouteDTO routeData);
    RouteDTO viewRouteData(UUID routeId);
    RouteDTO overwriteRouteData(RouteDTO routeData, UUID routeId);
    RouteDTO deleteRouteData(UUID routeId);

}

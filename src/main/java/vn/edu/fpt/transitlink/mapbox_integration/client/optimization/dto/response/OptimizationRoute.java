package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response;

import java.util.List;

public record OptimizationRoute(
        String vehicle,
        List<Stop> stops
) {}

package vn.edu.fpt.transitlink.mapbox_integration.client.direction.dto.response;

import java.util.List;

public record Waypoint(
        String name,
        List<Double> location, // [longitude, latitude]
        Double distance
) {
}

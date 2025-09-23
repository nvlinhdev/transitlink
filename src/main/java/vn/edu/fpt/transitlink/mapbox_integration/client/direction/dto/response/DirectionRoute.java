package vn.edu.fpt.transitlink.mapbox_integration.client.direction.dto.response;

import java.util.List;

public record DirectionRoute(
        Double duration,
        Double distance,
        String geometry,
        List<Leg> legs,
        List<Waypoint> waypoints
) {
}

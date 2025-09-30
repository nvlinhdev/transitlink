package vn.edu.fpt.transitlink.mapbox_integration.client.direction.dto.response;

import java.util.List;

public record Leg(
        double distance,
        double duration,
        List<Step> steps
) {
}

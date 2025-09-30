package vn.edu.fpt.transitlink.mapbox_integration.client.direction.dto.response;

import java.util.List;

public record DirectionResponse(
        String code,
        String uuid,
        List<DirectionRoute> routes
) {
}

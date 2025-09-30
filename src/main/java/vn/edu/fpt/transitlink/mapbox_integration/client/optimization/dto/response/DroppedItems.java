package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response;

import java.util.List;

public record DroppedItems(
        List<String> shipments
) {}

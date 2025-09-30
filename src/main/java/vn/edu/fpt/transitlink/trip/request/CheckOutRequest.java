package vn.edu.fpt.transitlink.trip.request;

import java.util.UUID;

public record CheckOutRequest(
        UUID routeId,
        Double latitude,
        Double longitude
) {
}

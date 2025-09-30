package vn.edu.fpt.transitlink.trip.request;

import java.util.UUID;

public record CheckInRequest(
        UUID routeId,
        Double latitude,
        Double longitude
) {
}

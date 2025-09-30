package vn.edu.fpt.transitlink.trip.request;

import java.util.UUID;

public record ConfirmPickupRequest(
        UUID passengerJourneyId,
        Double latitude,
        Double longitude
) {
}

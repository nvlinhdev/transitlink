package vn.edu.fpt.transitlink.trip.request;

import java.util.UUID;

public record ConfirmDropoffRequest(
        UUID passengerJourneyId,
        Double latitude,
        Double longitude
) {
}

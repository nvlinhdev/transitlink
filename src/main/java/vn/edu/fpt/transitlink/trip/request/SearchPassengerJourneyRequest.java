package vn.edu.fpt.transitlink.trip.request;

import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SearchPassengerJourneyRequest(
        String query, // Can search by passenger name, email, or journey ID
        JourneyStatus status,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        UUID pickupPlaceId,
        UUID dropoffPlaceId
) {
}

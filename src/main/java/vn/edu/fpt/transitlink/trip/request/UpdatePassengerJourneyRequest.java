package vn.edu.fpt.transitlink.trip.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import vn.edu.fpt.transitlink.identity.request.UpdatePassengerRequest;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdatePassengerJourneyRequest(
        UpdatePassengerRequest passengerInfo,
        UUID pickupPlaceId,
        UUID dropoffPlaceId,
        UUID routeId,
        @Future(message = "Latest stop arrival time must be in the future")
        OffsetDateTime lastestStopArrivalTime,
        @Size(min = 1, max = 16, message = "Seat count must be between 1 and 16")
        Integer seatCount,
        JourneyStatus status
) {
}

package vn.edu.fpt.transitlink.trip.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import vn.edu.fpt.transitlink.identity.request.CreatePassengerRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreatePassengerJourneyRequest(
        @NotNull(message = "Passenger information is required")
        CreatePassengerRequest passengerInfo,

        @NotNull(message = "Pickup place ID is required")
        UUID pickupPlaceId,

        @NotNull(message = "Dropoff place ID is required")
        UUID dropoffPlaceId,

        @Future(message = "Latest stop arrival time must be in the future")
        @NotNull(message = "Latest stop arrival time is required")
        OffsetDateTime lastestStopArrivalTime,

        @Size(min = 1, max = 16, message = "Seat count must be between 1 and 16")
        @NotNull(message = "Seat count is required")
        Integer seatCount
) {
}

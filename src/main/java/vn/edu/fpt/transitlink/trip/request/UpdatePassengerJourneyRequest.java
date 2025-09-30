package vn.edu.fpt.transitlink.trip.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import vn.edu.fpt.transitlink.identity.request.UpdatePassengerRequest;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdatePassengerJourneyRequest(
        UpdatePassengerRequest passengerInfo,
        UUID pickupPlaceId,
        UUID dropoffPlaceId,
        @Future(message = "Latest stop arrival time must be in the future")
        OffsetDateTime mainStopArrivalTime,
        @Min(1)
        Integer seatCount,
        JourneyType journeyType,
        String geometry,
        OffsetDateTime plannedPickupTime,
        OffsetDateTime actualPickupTime,
        OffsetDateTime plannedDropoffTime,
        OffsetDateTime actualDropoffTime,
        JourneyStatus status
) {
}

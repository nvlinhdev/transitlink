package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PassengerJourneyDetailForPassengerDTO(
        UUID id,
        PlaceDTO pickupPlace,
        PlaceDTO dropoffPlace,
        OffsetDateTime mainStopArrivalTime,
        Integer seatCount,
        JourneyType journeyType,
        String geometry,
        OffsetDateTime plannedPickupTime,
        OffsetDateTime plannedDropoffTime,
        JourneyStatus status
) {
}

package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PassengerJourneyDTO(
        UUID id,
        PassengerDTO passenger,
        PlaceDTO pickupPlace,
        PlaceDTO dropoffPlace,
        UUID routeId,
        OffsetDateTime lastestStopArrivalTime,
        OffsetDateTime actualPickupTime,
        OffsetDateTime actualDropoffTime,
        Integer seatCount,
        JourneyStatus status
) {
}

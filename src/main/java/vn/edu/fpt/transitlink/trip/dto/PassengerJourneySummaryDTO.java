package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PassengerJourneySummaryDTO (
         UUID id,
         String pickupPlace,
         String dropoffPlace,
         OffsetDateTime mainStopArrivalTime,
         Integer seatCount,
         JourneyType journeyType,
         OffsetDateTime plannedPickupTime,
         OffsetDateTime plannedDropoffTime,
         JourneyStatus status
) {
}

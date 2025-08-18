package vn.edu.fpt.transitlink.trip.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PassengerLocationDTO(
         UUID Id,
         UUID passengerId,
         UUID pickupPlace,
         UUID dropoffPlace,
         UUID routeId,
         OffsetDateTime lastestStopArrivalTime,
         OffsetDateTime actualPickupTime,
         OffsetDateTime actualDropoffTime,
         int seatCount,
         String status
) {
}

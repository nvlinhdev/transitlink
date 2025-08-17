package vn.edu.fpt.transitlink.trip.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TripCheckoutDTO(
        UUID tripId,
        UUID passengerId,
        OffsetDateTime time
) {

}

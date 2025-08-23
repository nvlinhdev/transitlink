package vn.edu.fpt.transitlink.trip.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StopDTO(
         UUID routeId,
         UUID placeId,
         int sequence,
         OffsetDateTime plannedDepartureTime,
         OffsetDateTime actualDepartureTime
) {
}

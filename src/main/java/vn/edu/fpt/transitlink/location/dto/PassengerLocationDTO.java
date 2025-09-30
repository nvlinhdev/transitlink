package vn.edu.fpt.transitlink.location.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PassengerLocationDTO(
        UUID passengerId,
        Double latitude,
        Double longitude,
        OffsetDateTime recordAt
) {
}

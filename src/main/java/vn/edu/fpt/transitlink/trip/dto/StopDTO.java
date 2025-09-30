package vn.edu.fpt.transitlink.trip.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StopDTO(
        UUID id,
        Double latitude,
        Double longitude,
        Integer sequence,
        OffsetDateTime plannedDepartureTime,
        OffsetDateTime actualDepartureTime,
        List<PassengerOnStopDTO> passengers
) {
}

package vn.edu.fpt.transitlink.trip.dto;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DriverLocationDTO(
         UUID Id,
         UUID driverId,
         OffsetDateTime recordAt,
         Point location,
         Float speedKmH,
         Float headingDeg,
         String status
) {
}

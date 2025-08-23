package vn.edu.fpt.transitlink.navigation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NavigationDTO(
        UUID tripId,
         String route,
         OffsetDateTime estimatedArrivalTime) {
}

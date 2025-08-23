package vn.edu.fpt.transitlink.location.dto;

import java.awt.*;
import java.util.UUID;

public record PassengerLocationDTO(
         UUID passengerLocationId,
         UUID passengerId,
         Point location,
         int recordAt,
         String status
) {
}

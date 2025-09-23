package vn.edu.fpt.transitlink.trip.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerJourneyData {
    UUID passengerJourneyId;
    OffsetDateTime plannedDepartureTime;
    OffsetDateTime plannedArrivalTime;
    String geometry;
}

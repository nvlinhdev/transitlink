package vn.edu.fpt.transitlink.trip.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TripCheckinDTO() {
     UUID triId;
     UUID passengerId;
     OffsetDateTime time;
}


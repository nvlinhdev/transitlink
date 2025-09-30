package vn.edu.fpt.transitlink.identity.dto;

import java.util.UUID;

public record PassengerDTO(
        UUID id,
        Integer totalCompletedTrips,
        Integer totalCancelledTrips,
        AccountDTO account
) {
}

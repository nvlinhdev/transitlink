package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdatePassengerRequest(
        @Schema(description = "Total number of completed trips", example = "15")
        Integer totalCompletedTrips,

        @Schema(description = "Total number of cancelled trips", example = "2")
        Integer totalCancelledTrips
) {
}

package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record UpdatePassengerRequest(

        @Schema(description = "Passenger account information")
        UpdateAccountRequest accountInfo,

        @Schema(example = "10", description = "Total completed trips for the passenger")
        @Min(value = 0, message = "Total completed trips must be non-negative")
        Integer totalCompletedTrips,

        @Schema(example = "2", description = "Total cancelled trips for the passenger")
        @Min(value = 0, message = "Total cancelled trips must be non-negative")
        Integer totalCancelledTrips
) {
}

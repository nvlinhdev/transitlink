package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public record CreatePassengerRequest(
        @NotNull(message = "Account ID is required")
        CreateAccountRequest accountInfo,

        @Schema(example = "10", description = "Total completed trips for the passenger")
        @Min(value = 0, message = "Total completed trips must be non-negative")
        Integer totalCompletedTrips,

        @Schema(example = "2", description = "Total cancelled trips for the passenger")
        @Min(value = 0, message = "Total cancelled trips must be non-negative")
        Integer totalCancelledTrips
) {
}

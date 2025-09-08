package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePassengerRequest(
        @NotNull(message = "Account ID is required")
        @Schema(description = "ID of the account to associate with this passenger", example = "a573aa20-f56b-4888-8b5b-88a7ad21b928")
        UUID accountId,

        @Schema(description = "Initial number of completed trips", example = "0")
        Integer totalCompletedTrips,

        @Schema(description = "Initial number of cancelled trips", example = "0")
        Integer totalCancelledTrips
) {
}

package vn.edu.fpt.transitlink.fleet.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request for creating a new depot")
public record CreateDepotRequest(
        @Schema(description = "Name of the depot", example = "Central Bus Depot", required = true)
        @NotBlank(message = "Depot name is required")
        @Size(min = 3, max = 100, message = "Depot name must be between 3 and 100 characters")
        String name,

        @Schema(description = "ID of an existing place to use as the depot's location", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
        UUID placeId,

        @Schema(description = "Name of the place if creating a new place", example = "Main Terminal", required = false)
        @Size(max = 100, message = "Place name cannot exceed 100 characters")
        String placeName,

        @Schema(description = "Address of the place if creating a new place", example = "123 Transport Street, City Center", required = false)
        @Size(max = 255, message = "Place address cannot exceed 255 characters")
        String placeAddress,

        @Schema(description = "Latitude coordinate if creating a new place", example = "10.762622", required = false)
        Double placeLatitude,

        @Schema(description = "Longitude coordinate if creating a new place", example = "106.660172", required = false)
        Double placeLongitude
) {
    public CreateDepotRequest {
        // Validation: Either placeId must be provided OR (placeName, placeLatitude, and placeLongitude) must be provided
        if (placeId == null) {
            if (placeName == null || placeLatitude == null || placeLongitude == null) {
                throw new IllegalArgumentException("Either placeId or (placeName, placeLatitude, and placeLongitude) must be provided");
            }
        }
    }
}

package vn.edu.fpt.transitlink.fleet.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request for updating an existing depot")
public record UpdateDepotRequest(
        @Schema(description = "New name of the depot", example = "North Bus Terminal", required = false)
        @Size(min = 3, max = 100, message = "Depot name must be between 3 and 100 characters")
        String name,

        @Schema(description = "ID of an existing place to use as the depot's new location", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
        UUID placeId,

        @Schema(description = "Name of the place if creating a new place", example = "North Terminal", required = false)
        @Size(max = 100, message = "Place name cannot exceed 100 characters")
        String placeName,

        @Schema(description = "Address of the place if creating a new place", example = "456 North Avenue, Downtown", required = false)
        @Size(max = 255, message = "Place address cannot exceed 255 characters")
        String placeAddress,

        @Schema(description = "Latitude coordinate if creating a new place", example = "10.823456", required = false)
        Double placeLatitude,

        @Schema(description = "Longitude coordinate if creating a new place", example = "106.789012", required = false)
        Double placeLongitude
) {
    public UpdateDepotRequest {
        // Validation: If trying to create a new place, all place details must be provided
        if (placeId == null && (placeName != null || placeLatitude != null || placeLongitude != null)) {
            if (placeName == null || placeLatitude == null || placeLongitude == null) {
                throw new IllegalArgumentException("When creating a new place, name, latitude, and longitude must all be provided");
            }
        }
    }
}

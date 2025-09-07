package vn.edu.fpt.transitlink.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Data Transfer Object for Place information")
public record PlaceDTO(
        @Schema(description = "Unique identifier of the place", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Name of the place", example = "Central Station")
        String name,

        @Schema(description = "Latitude coordinate of the place", example = "10.762622")
        Double latitude,

        @Schema(description = "Longitude coordinate of the place", example = "106.660172")
        Double longitude,

        @Schema(description = "Physical address of the place", example = "123 Transport Street, City Center")
        String address
) {}

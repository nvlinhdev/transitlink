package vn.edu.fpt.transitlink.fleet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;

import java.util.UUID;

@Schema(description = "Data Transfer Object for Depot information")
public record DepotDTO(
        @Schema(description = "Unique identifier of the depot", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Name of the depot", example = "Central Bus Depot")
        String name,

        @Schema(description = "Location information of the depot")
        PlaceDTO place
) {}

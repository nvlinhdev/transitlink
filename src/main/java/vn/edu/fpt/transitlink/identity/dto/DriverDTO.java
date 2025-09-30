package vn.edu.fpt.transitlink.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
import vn.edu.fpt.transitlink.identity.enumeration.LincenseClass;

import java.util.UUID;

public record DriverDTO(
        @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "Unique identifier of the driver")
        UUID id,

        @Schema(example = "123456789", description = "Driver's license number")
        String licenseNumber,

        @Schema(example = "B2", description = "License class of the driver")
        LincenseClass licenseClass,

        @Schema(description = "Account information of the driver")
        AccountDTO account,

        @Schema(description = "Depot information that the driver belongs to")
        DepotDTO depot
) {
}

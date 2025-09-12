package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.identity.enumeration.LincenseClass;

import java.util.UUID;

public record UpdateDriverRequest(
        @Schema(example = "123456789", description = "Driver's license number")
        String licenseNumber,

        @Schema(example = "B2", description = "License class of the driver")
        LincenseClass licenseClass,

        @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "ID of the depot the driver belongs to")
        UUID depotId
) {
}

package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.edu.fpt.transitlink.identity.enumeration.LincenseClass;

import java.util.UUID;

public record CreateDriverRequest(
        @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "Account ID of the driver")
        @NotNull(message = "Account ID is required")
        UUID accountId,

        @Schema(example = "123456789", description = "Driver's license number")
        @NotBlank(message = "License number is required")
        String licenseNumber,

        @Schema(example = "B2", description = "License class of the driver")
        @NotNull(message = "License class is required")
        LincenseClass licenseClass,

        @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "ID of the depot the driver belongs to")
        UUID depotId
) {}

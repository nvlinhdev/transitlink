package vn.edu.fpt.transitlink.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.util.UUID;

public record RoleDTO(
        @Schema(example = "e8f56e51-c6da-4863-bc1b-7474874d26fe", description = "Unique identifier of the role")
        UUID id,

        @Schema(example = "MANAGER", description = "System name of the role")
        RoleName name,

        @Schema(example = "manager", description = "Display name of the role")
        String displayName
) {}

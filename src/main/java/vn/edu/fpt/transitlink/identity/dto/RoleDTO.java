package vn.edu.fpt.transitlink.identity.dto;

import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.util.UUID;

public record RoleDTO(
        UUID id,
        RoleName name,
        String displayName
) {}

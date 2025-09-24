package vn.edu.fpt.transitlink.identity.dto;

import vn.edu.fpt.transitlink.identity.enumeration.LincenseClass;

import java.util.UUID;

public record DriverInfo(
        UUID id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String licenseNumber,
        LincenseClass licenseClass,
        String avatarUrl
) {
}

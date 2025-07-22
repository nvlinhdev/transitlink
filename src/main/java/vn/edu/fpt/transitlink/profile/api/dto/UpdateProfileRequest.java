package vn.edu.fpt.transitlink.profile.api.dto;

public record UpdateProfileRequest(
        String displayName,
        String avatarUrl,
        String phoneNumber,
        String address
) {}

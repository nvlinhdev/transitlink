package vn.edu.fpt.transitlink.profile.api.dto;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        UUID accountId,
        String firstName,
        String lastName,
        String phoneNumber,
        String gender,
        String zaloUrl,
        String avatarUrl
) {}

package vn.edu.fpt.transitlink.profile.presentation.dto;

import java.util.UUID;

public record UserProfileDTO(
        UUID id,
        UUID accountId,
        String firstName,
        String lastName,
        String phoneNumber,
        String gender,
        String zaloPhoneNumber,
        String avatarUrl
) {}

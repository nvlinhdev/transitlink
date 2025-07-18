package vn.edu.fpt.transitlink.profile.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        String gender,
        String zaloUrl,
        String avatarUrl
) {}
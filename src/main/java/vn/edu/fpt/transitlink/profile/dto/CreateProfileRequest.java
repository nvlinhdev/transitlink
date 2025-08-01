package vn.edu.fpt.transitlink.profile.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        String gender,
        String zaloPhoneNumber,
        String avatarUrl
) {}
package vn.edu.fpt.transitlink.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleTokenRequest(
        @NotBlank(message = "ID Token must not be empty")
        String idToken
) {}

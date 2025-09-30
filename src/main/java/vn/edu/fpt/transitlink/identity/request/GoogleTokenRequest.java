package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoogleTokenRequest(
        @Schema(example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjI...", description = "Google authentication ID token")
        @NotBlank(message = "ID Token must not be empty")
        String idToken
) {}

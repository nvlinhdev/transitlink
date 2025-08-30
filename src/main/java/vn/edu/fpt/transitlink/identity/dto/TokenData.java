package vn.edu.fpt.transitlink.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenData(
        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT access token for API authorization")
        String accessToken,

        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "JWT refresh token for obtaining new access tokens")
        String refreshToken,

        @Schema(example = "3600", description = "Number of seconds until the access token expires")
        long expiresInSeconds
) {}

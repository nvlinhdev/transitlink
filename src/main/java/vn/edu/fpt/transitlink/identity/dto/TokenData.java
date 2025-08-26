package vn.edu.fpt.transitlink.identity.dto;

public record TokenData(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {}

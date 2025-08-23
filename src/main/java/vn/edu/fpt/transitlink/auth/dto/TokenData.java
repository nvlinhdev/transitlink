package vn.edu.fpt.transitlink.auth.dto;

public record TokenData(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {

    public static TokenData from(String accessToken, String refreshToken, long expiresInSeconds) {
        return new TokenData(accessToken, refreshToken, expiresInSeconds);
    }
}

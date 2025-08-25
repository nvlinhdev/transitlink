package vn.edu.fpt.transitlink.identity.dto;

public record TokenData(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {

    public static TokenData from(String accessToken, String refreshToken, long expiresInSeconds) {
        return new TokenData(accessToken, refreshToken, expiresInSeconds);
    }
}

package vn.edu.fpt.transitlink.profile.spi.dto;

public record PhoneVerificationResult(
        boolean valid,
        String phoneNumber
) {
    public static PhoneVerificationResult valid(String phoneNumber) {
        return new PhoneVerificationResult(true, phoneNumber);
    }

    public static PhoneVerificationResult invalid() {
        return new PhoneVerificationResult(false, null);
    }
}
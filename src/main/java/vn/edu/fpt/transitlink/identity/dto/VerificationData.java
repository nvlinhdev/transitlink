package vn.edu.fpt.transitlink.identity.dto;

import vn.edu.fpt.transitlink.identity.enumeration.VerificationType;

import java.time.LocalDateTime;

public record VerificationData(
        String email,
        String token,
        String otp,
        VerificationType type,
        LocalDateTime expiryTime
) {}

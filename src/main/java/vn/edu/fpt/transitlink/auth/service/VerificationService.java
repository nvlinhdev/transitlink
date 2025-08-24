package vn.edu.fpt.transitlink.auth.service;

import vn.edu.fpt.transitlink.auth.dto.VerificationData;
import vn.edu.fpt.transitlink.auth.enumeration.VerificationType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface VerificationService {

    CompletableFuture<Boolean> sendVerificationEmail(String email, VerificationType type, Map<String, Object> templateVariables);
    boolean verifyOtp(String email, String otp, VerificationType type);
    boolean verifyToken(String token, VerificationType type);
}


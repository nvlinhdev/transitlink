package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.enumeration.VerificationType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VerificationService {

    CompletableFuture<Boolean> sendVerificationEmail(String email, VerificationType type, Map<String, Object> templateVariables);
    boolean verifyOtp(String email, String otp, VerificationType type);
    boolean verifyToken(String token, VerificationType type);
}


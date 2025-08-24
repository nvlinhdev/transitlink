package vn.edu.fpt.transitlink.auth.service;

import vn.edu.fpt.transitlink.auth.dto.RegisterRequest;
import vn.edu.fpt.transitlink.auth.dto.ResendVerificationRequest;
import vn.edu.fpt.transitlink.auth.dto.VerifyEmailRequest;

public interface RegistrationService {
    boolean register(RegisterRequest registerRequest);
    boolean verifyEmail(VerifyEmailRequest request);
    void resendVerificationEmail(ResendVerificationRequest request);
}

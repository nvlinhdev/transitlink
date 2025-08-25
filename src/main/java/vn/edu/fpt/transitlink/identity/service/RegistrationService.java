package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.RegisterRequest;
import vn.edu.fpt.transitlink.identity.dto.ResendVerificationRequest;
import vn.edu.fpt.transitlink.identity.dto.VerifyEmailRequest;

public interface RegistrationService {
    boolean register(RegisterRequest registerRequest);
    boolean verifyEmail(VerifyEmailRequest request);
    void resendVerificationEmail(ResendVerificationRequest request);
}

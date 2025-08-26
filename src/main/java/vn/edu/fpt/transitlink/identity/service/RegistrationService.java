package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.request.RegisterRequest;
import vn.edu.fpt.transitlink.identity.request.ResendVerificationRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailRequest;

public interface RegistrationService {
    boolean register(RegisterRequest registerRequest);
    boolean verifyEmail(VerifyEmailRequest request);
    void resendVerificationEmail(ResendVerificationRequest request);
}

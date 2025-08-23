package vn.edu.fpt.transitlink.auth.service;

import vn.edu.fpt.transitlink.auth.dto.ResendVerificationRequest;
import vn.edu.fpt.transitlink.auth.dto.VerifyEmailRequest;

public interface EmailVerificationService {
    public void sendVerificationEmail(String email, String firstName, String lastName);
    boolean verifyEmail(VerifyEmailRequest request);
    boolean verifyEmailByUrl(String token);
    void resendVerificationEmail(ResendVerificationRequest request);
}
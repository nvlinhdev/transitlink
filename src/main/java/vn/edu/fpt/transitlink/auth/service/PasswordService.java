package vn.edu.fpt.transitlink.auth.service;

import vn.edu.fpt.transitlink.auth.dto.ForgotPasswordRequest;
import vn.edu.fpt.transitlink.auth.dto.ResetPasswordRequest;

public interface PasswordService {
    void sendResetPasswordEmail(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}

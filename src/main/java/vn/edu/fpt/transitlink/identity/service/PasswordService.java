package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.ChangePasswordRequest;
import vn.edu.fpt.transitlink.identity.dto.ForgotPasswordRequest;
import vn.edu.fpt.transitlink.identity.dto.ResetPasswordRequest;
import vn.edu.fpt.transitlink.identity.dto.SetPasswordRequest;

public interface PasswordService {
    void sendResetPasswordEmail(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void setPassword(String email, SetPasswordRequest request);
}

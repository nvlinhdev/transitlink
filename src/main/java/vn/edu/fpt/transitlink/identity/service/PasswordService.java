package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.request.ChangePasswordRequest;
import vn.edu.fpt.transitlink.identity.request.ForgotPasswordRequest;
import vn.edu.fpt.transitlink.identity.request.ResetPasswordRequest;
import vn.edu.fpt.transitlink.identity.request.SetPasswordRequest;

public interface PasswordService {
    void sendResetPasswordEmail(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void setPassword(String email, SetPasswordRequest request);
}

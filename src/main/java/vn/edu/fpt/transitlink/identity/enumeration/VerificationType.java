package vn.edu.fpt.transitlink.identity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VerificationType {
    ACCOUNT_ACTIVATION("active_otp:", "active_token:", "/verify-email", "email-verification", "Xác thực Email TransitLink"),
    PASSWORD_RESET("reset_otp:", "reset_token:", "/reset-password", "password-reset", "Đặt lại mật khẩu TransitLink"),
    EMAIL_CHANGE("email_change_otp:", "email_change_token:", "/verify-email-change", "email-change-verification", "Xác thực thay đổi Email TransitLink");

    private final String otpPrefix;
    private final String tokenPrefix;
    private final String urlPath;
    private final String emailTemplate;
    private final String emailSubject;
}
package vn.edu.fpt.transitlink.auth.exception;

import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

public enum AuthErrorCode implements ErrorCodeDefinition {
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Token is invalid", HttpStatus.UNAUTHORIZED ),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid email/phone number or password", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Role not found", HttpStatus.NOT_FOUND),
    // Add these to your AuthErrorCode enum
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "Too many requests, please try again later", HttpStatus.TOO_MANY_REQUESTS),
    INVALID_OR_EXPIRED_OTP("INVALID_OR_EXPIRED_OTP", "Invalid or expired OTP", HttpStatus.BAD_REQUEST),
    OTP_BLOCKED("OTP_BLOCKED", "OTP blocked due to too many failed attempts", HttpStatus.FORBIDDEN),
    EMAIL_ALREADY_VERIFIED("EMAIL_ALREADY_VERIFIED", "Email is already verified", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "Account not found", HttpStatus.NOT_FOUND)
    ;
    private String code;
    private String defaultMessage;
    private HttpStatus httpStatus;

    AuthErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

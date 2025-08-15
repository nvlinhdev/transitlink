package vn.edu.fpt.transitlink.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum ProfileErrorCode implements ErrorCodeDefinition {
    // ========== Profile CRUD Operations ==========
    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND", "Profile not found", HttpStatus.NOT_FOUND),
    PROFILE_ALREADY_EXISTS("PROFILE_ALREADY_EXISTS", "Profile already exists", HttpStatus.CONFLICT),
    PROFILE_ACCESS_DENIED("PROFILE_ACCESS_DENIED", "Access denied to profile", HttpStatus.FORBIDDEN),

    // ========== Profile Operations ==========
    PROFILE_UPDATE_FAILED("PROFILE_UPDATE_FAILED", "Profile update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_DELETION_FAILED("PROFILE_DELETION_FAILED", "Profile deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_CREATION_FAILED("PROFILE_CREATION_FAILED", "Profile creation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // ========== Phone Verification - Token Related ==========
    PHONE_VERIFICATION_FAILED("PHONE_VERIFICATION_FAILED", "Phone verification failed", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_TOKEN("INVALID_PHONE_TOKEN", "Invalid phone verification token", HttpStatus.BAD_REQUEST),
    PHONE_TOKEN_EXPIRED("PHONE_TOKEN_EXPIRED", "Phone verification token expired", HttpStatus.BAD_REQUEST),
    PHONE_TOKEN_MALFORMED("PHONE_TOKEN_MALFORMED", "Phone verification token is malformed", HttpStatus.BAD_REQUEST),

    // ========== Phone Verification - Code/OTP Related ==========
    PHONE_VERIFICATION_CODE_EXPIRED("PHONE_VERIFICATION_CODE_EXPIRED", "Phone verification code expired", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE("INVALID_VERIFICATION_CODE", "Invalid verification code", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_ATTEMPTS_EXCEEDED("VERIFICATION_CODE_ATTEMPTS_EXCEEDED", "Maximum verification attempts exceeded", HttpStatus.TOO_MANY_REQUESTS),

    // ========== Phone Verification - Service Related ==========
    PHONE_VERIFICATION_SERVICE_UNAVAILABLE("PHONE_VERIFICATION_SERVICE_UNAVAILABLE", "Phone verification service is currently unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    PHONE_VERIFICATION_PROVIDER_ERROR("PHONE_VERIFICATION_PROVIDER_ERROR", "Phone verification provider error", HttpStatus.BAD_GATEWAY),

    // ========== Authorization & Permission ==========
    INSUFFICIENT_PERMISSIONS("INSUFFICIENT_PERMISSIONS", "Insufficient permissions to perform this operation", HttpStatus.FORBIDDEN),
    PROFILE_OWNER_MISMATCH("PROFILE_OWNER_MISMATCH", "Profile does not belong to the current user", HttpStatus.FORBIDDEN),

    // ========== Rate Limiting ==========
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "Too many requests, please try again later", HttpStatus.TOO_MANY_REQUESTS),
    PHONE_VERIFICATION_RATE_LIMIT_EXCEEDED("PHONE_VERIFICATION_RATE_LIMIT_EXCEEDED", "Phone verification rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProfileErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

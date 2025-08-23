package vn.edu.fpt.transitlink.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode {

    //Profile
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
    PHONE_VERIFICATION_RATE_LIMIT_EXCEEDED("PHONE_VERIFICATION_RATE_LIMIT_EXCEEDED", "Phone verification rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS),

    //Driver
    DRIVER_NOT_FOUND("DRIVER_NOT_FOUND", "Driver not found", HttpStatus.NOT_FOUND),
    DRIVER_ALREADY_EXISTS("DRIVER_ALREADY_EXISTS", "Driver already exists", HttpStatus.CONFLICT),
    DRIVER_ACCESS_DENIED("DRIVER_ACCESS_DENIED", "Access denied to driver", HttpStatus.FORBIDDEN),
    DRIVER_UPDATE_FAILED("DRIVER_UPDATE_FAILED", "Driver update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DRIVER_DELETION_FAILED("DRIVER_DELETION_FAILED", "Driver deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DRIVER_CREATION_FAILED("DRIVER_CREATION_FAILED", "Driver creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DRIVER_NOT_FOUND_BY_ID("DRIVER_NOT_FOUND_BY_ID", "Driver not found by id", HttpStatus.NOT_FOUND),
    DRIVER_NOT_FOUND_BY_EMAIL("DRIVER_NOT_FOUND_BY_EMAIL", "Driver not found by email", HttpStatus.NOT_FOUND),
    DRIVER_NOT_FOUND_BY_PHONE("DRIVER_NOT_FOUND_BY_PHONE", "Driver not found by phone", HttpStatus.NOT_FOUND),
    DRIVER_NOT_FOUND_BY_LICENSE_NUMBER("DRIVER_NOT_FOUND_BY_LICENSE_NUMBER", "Driver not found by license number", HttpStatus.NOT_FOUND),
    DRIVER_NOT_FOUND_BY_STATUS("DRIVER_NOT_FOUND_BY_STATUS", "Driver not found by status", HttpStatus.NOT_FOUND),
    DRIVER_NOT_FOUND_BY_NAME("DRIVER_NOT_FOUND_BY_NAME", "Driver not found by name", HttpStatus.NOT_FOUND),

    //Passenger
    PASSENGER_NOT_FOUND("PASSENGER_NOT_FOUND", "Passenger not found", HttpStatus.NOT_FOUND),
    PASSENGER_ALREADY_EXISTS("PASSENGER_ALREADY_EXISTS", "Passenger already exists", HttpStatus.CONFLICT),
    PASSENGER_ACCESS_DENIED("PASSENGER_ACCESS_DENIED", "Access denied to passenger", HttpStatus.FORBIDDEN),
    PASSENGER_UPDATE_FAILED("PASSENGER_UPDATE_FAILED", "Passenger update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_DELETION_FAILED("PASSENGER_DELETION_FAILED", "Passenger deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_CREATION_FAILED("PASSENGER_CREATION_FAILED", "Passenger creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    UserErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

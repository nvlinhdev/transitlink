package vn.edu.fpt.transitlink.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum ProfileErrorCode implements ErrorCodeDefinition {
    PROFILE_ALREADY_EXISTS("PROFILE_ALREADY_EXISTS", "Profile already exists", HttpStatus.CONFLICT),
    PHONE_NUMBER_NOT_IN_TOKEN("PHONE_NUMBER_NOT_IN_TOKEN", "Phone number not found in Firebase token", HttpStatus.BAD_REQUEST),
    PHONE_VERIFICATION_TOKEN_EXPIRED("PHONE_VERIFICATION_TOKEN_EXPIRED", "ID token expired for verification purposes", HttpStatus.UNAUTHORIZED),
    PHONE_VERIFICATION_ALREADY_USED("PHONE_VERIFICATION_ALREADY_USED", "This phone verification token has already been used", HttpStatus.BAD_REQUEST),
    PHONE_VERIFICATION_FAILED("PHONE_VERIFICATION_FAILED", "Firebase token verification failed", HttpStatus.UNAUTHORIZED),
    PHONE_VERIFICATION_TOKEN_INVALID("PHONE_VERIFICATION_TOKEN_INVALID", "Invalid Firebase phone verification token", HttpStatus.BAD_REQUEST);;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProfileErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

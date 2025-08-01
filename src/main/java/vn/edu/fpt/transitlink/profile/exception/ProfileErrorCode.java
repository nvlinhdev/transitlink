package vn.edu.fpt.transitlink.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum ProfileErrorCode implements ErrorCodeDefinition {
    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND", "Profile not found", HttpStatus.NOT_FOUND),
    PROFILE_ALREADY_EXISTS("PROFILE_ALREADY_EXISTS", "Profile already exists", HttpStatus.CONFLICT),
    PROFILE_UPDATE_FAILED("PROFILE_UPDATE_FAILED", "Profile update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_DELETE_FAILED("PROFILE_DELETE_FAILED", "Profile delete failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProfileErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

package vn.edu.fpt.transitlink.navigation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NavigationErrorCode {

    NAVIGATION_NOT_FOUND("NAVIGATION_NOT_FOUND", "Navigation not found", HttpStatus.NOT_FOUND),
    NAVIGATION_ALREADY_EXISTS("NAVIGATION_ALREADY_EXISTS", "Navigation already exists", HttpStatus.CONFLICT),
    NAVIGATION_ACCESS_DENIED("NAVIGATION_ACCESS_DENIED", "Access denied to navigation", HttpStatus.FORBIDDEN),
    NAVIGATION_UPDATE_FAILED("NAVIGATION_UPDATE_FAILED", "Navigation update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NAVIGATION_DELETION_FAILED("NAVIGATION_DELETION_FAILED", "Navigation deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NAVIGATION_CREATION_FAILED("NAVIGATION_CREATION_FAILED", "Navigation creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    NavigationErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

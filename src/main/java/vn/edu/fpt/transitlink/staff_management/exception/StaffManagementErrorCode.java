package vn.edu.fpt.transitlink.staff_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum StaffManagementErrorCode implements ErrorCodeDefinition {
    STAFF_NOT_FOUND("STAFF_NOT_FOUND", "Staff not found", HttpStatus.NOT_FOUND),
    STAFF_ALREADY_EXISTS("STAFF_ALREADY_EXISTS", "Staff already exists", HttpStatus.CONFLICT),
    STAFF_ACCESS_DENIED("STAFF_ACCESS_DENIED", "Access denied to staff", HttpStatus.FORBIDDEN),
    STAFF_UPDATE_FAILED("STAFF_UPDATE_FAILED", "Staff update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    STAFF_DELETION_FAILED("STAFF_DELETION_FAILED", "Staff deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    STAFF_CREATION_FAILED("STAFF_CREATION_FAILED", "Staff creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    StaffManagementErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

package vn.edu.fpt.transitlink.schedule.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum SchheduleErrorCode implements ErrorCodeDefinition {

    SCHEDULE_NOT_FOUND("SCHEDULE_NOT_FOUND", "Schedule not found", HttpStatus.NOT_FOUND),
    SCHEDULE_ALREADY_EXISTS("SCHEDULE_ALREADY_EXISTS", "Schedule already exists", HttpStatus.CONFLICT),
    SCHEDULE_ACCESS_DENIED("SCHEDULE_ACCESS_DENIED", "Access denied to schedule", HttpStatus.FORBIDDEN),
    SCHEDULE_UPDATE_FAILED("SCHEDULE_UPDATE_FAILED", "Schedule update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SCHEDULE_DELETION_FAILED("SCHEDULE_DELETION_FAILED", "Schedule deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SCHEDULE_CREATION_FAILED("SCHEDULE_CREATION_FAILED", "Schedule creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    SchheduleErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

package vn.edu.fpt.transitlink.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum NotificationErrorCode implements ErrorCodeDefinition {
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "Notification not found", HttpStatus.NOT_FOUND),
    SEND_NOTIFICATION_FAILED("SEND_NOTIFICATION_FAILED", "Failed to send notification", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    NotificationErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

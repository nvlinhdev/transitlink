package vn.edu.fpt.transitlink.monitoring.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum MonitoringErrorCode implements ErrorCodeDefinition {

    MONITORING_NOT_FOUND("MONITORING_NOT_FOUND", "Monitoring not found", HttpStatus.NOT_FOUND),
    MONITORING_ALREADY_EXISTS("MONITORING_ALREADY_EXISTS", "Monitoring already exists", HttpStatus.CONFLICT),
    MONITORING_ACCESS_DENIED("MONITORING_ACCESS_DENIED", "Access denied to monitoring", HttpStatus.FORBIDDEN),
    MONITORING_UPDATE_FAILED("MONITORING_UPDATE_FAILED", "Monitoring update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    MONITORING_DELETION_FAILED("MONITORING_DELETION_FAILED", "Monitoring deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    MONITORING_CREATION_FAILED("MONITORING_CREATION_FAILED", "Monitoring creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    MonitoringErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

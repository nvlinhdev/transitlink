package vn.edu.fpt.transitlink.reporting.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReportErrorCode {

    REPORT_NOT_FOUND("REPORT_NOT_FOUND", "Report not found", HttpStatus.NOT_FOUND),
    REPORT_ALREADY_EXISTS("REPORT_ALREADY_EXISTS", "Report already exists", HttpStatus.CONFLICT),
    REPORT_ACCESS_DENIED("REPORT_ACCESS_DENIED", "Access denied to report", HttpStatus.FORBIDDEN),
    REPORT_UPDATE_FAILED("REPORT_UPDATE_FAILED", "Report update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    REPORT_DELETION_FAILED("REPORT_DELETION_FAILED", "Report deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    REPORT_CREATION_FAILED("REPORT_CREATION_FAILED", "Report creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ReportErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

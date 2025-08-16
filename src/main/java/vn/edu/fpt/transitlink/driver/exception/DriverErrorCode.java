package vn.edu.fpt.transitlink.driver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum DriverErrorCode implements ErrorCodeDefinition {

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
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    DriverErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

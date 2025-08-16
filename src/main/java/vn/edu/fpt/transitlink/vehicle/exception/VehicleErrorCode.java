package vn.edu.fpt.transitlink.vehicle.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum VehicleErrorCode implements ErrorCodeDefinition {

    VEHICLE_NOT_FOUND("VEHICLE_NOT_FOUND", "Vehicle not found", HttpStatus.NOT_FOUND),
    VEHICLE_ALREADY_EXISTS("VEHICLE_ALREADY_EXISTS", "Vehicle already exists", HttpStatus.CONFLICT),
    VEHICLE_ACCESS_DENIED("VEHICLE_ACCESS_DENIED", "Access denied to vehicle", HttpStatus.FORBIDDEN),
    VEHICLE_UPDATE_FAILED("VEHICLE_UPDATE_FAILED", "Vehicle update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    VEHICLE_DELETION_FAILED("VEHICLE_DELETION_FAILED", "Vehicle deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    VEHICLE_CREATION_FAILED("VEHICLE_CREATION_FAILED", "Vehicle creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    VehicleErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

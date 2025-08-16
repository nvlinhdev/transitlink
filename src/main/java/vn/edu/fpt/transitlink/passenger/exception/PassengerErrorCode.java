package vn.edu.fpt.transitlink.passenger.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum PassengerErrorCode implements ErrorCodeDefinition {
    PASSENGER_NOT_FOUND("PASSENGER_NOT_FOUND", "Passenger not found", HttpStatus.NOT_FOUND),
    PASSENGER_ALREADY_EXISTS("PASSENGER_ALREADY_EXISTS", "Passenger already exists", HttpStatus.CONFLICT),
    PASSENGER_ACCESS_DENIED("PASSENGER_ACCESS_DENIED", "Access denied to passenger", HttpStatus.FORBIDDEN),
    PASSENGER_UPDATE_FAILED("PASSENGER_UPDATE_FAILED", "Passenger update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_DELETION_FAILED("PASSENGER_DELETION_FAILED", "Passenger deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_CREATION_FAILED("PASSENGER_CREATION_FAILED", "Passenger creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    PassengerErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

package vn.edu.fpt.transitlink.trip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TripErrorCode {

    TRIP_NOT_FOUND("TRIP_NOT_FOUND", "Trip not found", HttpStatus.NOT_FOUND),
    TRIP_ALREADY_EXISTS("TRIP_ALREADY_EXISTS", "Trip already exists", HttpStatus.CONFLICT),
    TRIP_ACCESS_DENIED("TRIP_ACCESS_DENIED", "Access denied to trip", HttpStatus.FORBIDDEN),
    TRIP_UPDATE_FAILED("TRIP_UPDATE_FAILED", "Trip update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TRIP_DELETION_FAILED("TRIP_DELETION_FAILED", "Trip deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TRIP_CREATION_FAILED("TRIP_CREATION_FAILED", "Trip creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    TripErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

package vn.edu.fpt.transitlink.trip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum TripErrorCode implements ErrorCodeDefinition {

    //Stop

    //PassengerJourney
    PASSENGER_JOURNEY_NOT_FOUND("PASSENGER_JOURNEY_NOT_FOUND", "Passenger journey not found", HttpStatus.NOT_FOUND),
    PASSENGER_JOURNEY_ALREADY_EXISTS("PASSENGER_JOURNEY_ALREADY_EXISTS", "Passenger journey already exists", HttpStatus.CONFLICT),
    PASSENGER_JOURNEY_ACCESS_DENIED("PASSENGER_JOURNEY_ACCESS_DENIED", "Access denied to passenger journey", HttpStatus.FORBIDDEN),
    PASSENGER_JOURNEY_UPDATE_FAILED("PASSENGER_JOURNEY_UPDATE_FAILED", "Passenger journey update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_JOURNEY_DELETION_FAILED("PASSENGER_JOURNEY_DELETION_FAILED", "Passenger journey deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_JOURNEY_CREATION_FAILED("PASSENGER_JOURNEY_CREATION_FAILED", "Passenger journey creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_JOURNEY_STATUS("INVALID_JOURNEY_STATUS", "Invalid journey status", HttpStatus.BAD_REQUEST),
    INVALID_SEAT_COUNT("INVALID_SEAT_COUNT", "Seat count must be positive", HttpStatus.BAD_REQUEST),
    JOURNEY_CANNOT_BE_MODIFIED("JOURNEY_CANNOT_BE_MODIFIED", "Journey cannot be modified in current status", HttpStatus.BAD_REQUEST),
    EXCEL_IMPORT_FAILED("EXCEL_IMPORT_FAILED", "Excel file import failed", HttpStatus.BAD_REQUEST),

    //Routes
    ROUTE_NOT_FOUND("ROUTE_NOT_FOUND", "Route not found", HttpStatus.NOT_FOUND),
    ROUTE_ALREADY_EXISTS("ROUTE_ALREADY_EXISTS", "Route already exists", HttpStatus.CONFLICT),
    ROUTE_ACCESS_DENIED("ROUTE_ACCESS_DENIED", "Access denied to route", HttpStatus.FORBIDDEN),
    ROUTE_UPDATE_FAILED("ROUTE_UPDATE_FAILED", "Route update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ROUTE_DELETION_FAILED("ROUTE_DELETION_FAILED", "Route deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ROUTE_CREATION_FAILED("ROUTE_CREATION_FAILED", "Route creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
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

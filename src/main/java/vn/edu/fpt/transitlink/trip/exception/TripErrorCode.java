package vn.edu.fpt.transitlink.trip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum TripErrorCode implements ErrorCodeDefinition {

    //Stop

    //PassengerJourney
    PASSENGER_JOURNEY_NOT_FOUND("PASSENGER_JOURNEY_NOT_FOUND", "Passenger journey not found", HttpStatus.NOT_FOUND),
    PASSENGER_JOURNEY_UPDATE_FAILED("PASSENGER_JOURNEY_UPDATE_FAILED", "Passenger journey update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_JOURNEY_DELETION_FAILED("PASSENGER_JOURNEY_DELETION_FAILED", "Passenger journey deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_JOURNEY_CREATION_FAILED("PASSENGER_JOURNEY_CREATION_FAILED", "Passenger journey creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    JOURNEY_CANNOT_BE_MODIFIED("JOURNEY_CANNOT_BE_MODIFIED", "Journey cannot be modified in current status", HttpStatus.BAD_REQUEST),
    EXCEL_IMPORT_FAILED("EXCEL_IMPORT_FAILED", "Excel file import failed", HttpStatus.BAD_REQUEST),
    PASSENGER_JOURNEY_STATUS_INVALID("PASSENGER_JOURNEY_STATUS_INVALID", "Passenger journey status is invalid for this operation", HttpStatus.BAD_REQUEST),
    PASSENGER_JOURNEY_NO_STOPS_FOUND("PASSENGER_JOURNEY_NO_STOPS_FOUND", "No stops found for passenger journey", HttpStatus.BAD_REQUEST),
    PASSENGER_JOURNEY_PICKUP_TOO_FAR("PASSENGER_JOURNEY_PICKUP_TOO_FAR", "Passenger is too far from pickup location", HttpStatus.BAD_REQUEST),
    PASSENGER_JOURNEY_DROPOFF_TOO_FAR("PASSENGER_JOURNEY_DROPOFF_TOO_FAR", "Passenger is too far from dropoff location", HttpStatus.BAD_REQUEST),


    //Routes
    ROUTE_NOT_FOUND("ROUTE_NOT_FOUND", "Route not found", HttpStatus.NOT_FOUND),
    DRIVER_HAS_OVERLAPPING_ROUTE("DRIVER_HAS_OVERLAPPING_ROUTE", "Driver has overlapping route", HttpStatus.BAD_REQUEST),
    ROUTE_UPDATE_FAILED("ROUTE_UPDATE_FAILED", "Route update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ROUTE_STATUS_INVALID_FOR_CHECKIN("ROUTE_STATUS_INVALID_FOR_CHECKIN", "Route status is invalid for check-in", HttpStatus.BAD_REQUEST),
    ROUTE_CHECKIN_TIME_WINDOW_VIOLATED("ROUTE_CHECKIN_TIME_WINDOW_VIOLATED", "Check-in time window violated", HttpStatus.BAD_REQUEST),
    ROUTE_HAS_NO_STOPS("ROUTE_HAS_NO_STOPS", "Route has no stops", HttpStatus.BAD_REQUEST),
    ROUTE_CHECKIN_LOCATION_VIOLATED("ROUTE_CHECKIN_LOCATION_VIOLATED", "Check-in location violated", HttpStatus.BAD_REQUEST),
    ROUTE_STATUS_INVALID_FOR_CHECKOUT("ROUTE_STATUS_INVALID_FOR_CHECKOUT", "Route status is invalid for check-out", HttpStatus.BAD_REQUEST),
    ROUTE_CHECKOUT_LOCATION_VIOLATED("ROUTE_CHECKOUT_LOCATION_VIOLATED", "Check-out location violated", HttpStatus.BAD_REQUEST),
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

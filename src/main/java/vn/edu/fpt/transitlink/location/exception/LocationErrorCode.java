package vn.edu.fpt.transitlink.location.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum LocationErrorCode {

    //PassengerLocation
    PASSENGER_LOCATION_NOT_FOUND("PASSENGER_LOCATION_NOT_FOUND", "Passenger location not found", HttpStatus.NOT_FOUND),
    PASSENGER_LOCATION_ALREADY_EXISTS("PASSENGER_LOCATION_ALREADY_EXISTS", "Passenger location already exists", HttpStatus.CONFLICT),
    PASSENGER_LOCATION_ACCESS_DENIED("PASSENGER_LOCATION_ACCESS_DENIED", "Access denied to passenger location", HttpStatus.FORBIDDEN),
    PASSENGER_LOCATION_UPDATE_FAILED("PASSENGER_LOCATION_UPDATE_FAILED", "Passenger location update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_LOCATION_DELETION_FAILED("PASSENGER_LOCATION_DELETION_FAILED", "Passenger location deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSENGER_LOCATION_CREATION_FAILED("PASSENGER_LOCATION_CREATION_FAILED", "Passenger location creation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    //Place
    PLACE_NOT_FOUND("PLACE_NOT_FOUND", "Place not found", HttpStatus.NOT_FOUND),
    PLACE_ALREADY_EXISTS("PLACE_ALREADY_EXISTS", "Place already exists", HttpStatus.CONFLICT),
    PLACE_ACCESS_DENIED("PLACE_ACCESS_DENIED", "Access denied to place", HttpStatus.FORBIDDEN),
    PLACE_UPDATE_FAILED("PLACE_UPDATE_FAILED", "Place update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PLACE_DELETION_FAILED("PLACE_DELETION_FAILED", "Place deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PLACE_CREATION_FAILED("PLACE_CREATION_FAILED", "Place creation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    //DriverLocation
    DRIVER_LOCATION_NOT_FOUND("DRIVER_LOCATION_NOT_FOUND", "Driver location not found", HttpStatus.NOT_FOUND),
    DRIVER_LOCATION_ALREADY_EXISTS("DRIVER_LOCATION_ALREADY_EXISTS", "Driver location already exists", HttpStatus.CONFLICT),
    DRIVER_LOCATION_ACCESS_DENIED("DRIVER_LOCATION_ACCESS_DENIED", "Access denied to driver location", HttpStatus.FORBIDDEN),
    DRIVER_LOCATION_UPDATE_FAILED("DRIVER_LOCATION_UPDATE_FAILED", "Driver location update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DRIVER_LOCATION_DELETION_FAILED("DRIVER_LOCATION_DELETION_FAILED", "Driver location deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DRIVER_LOCATION_CREATION_FAILED("DRIVER_LOCATION_CREATION_FAILED", "Driver location creation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    LocationErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

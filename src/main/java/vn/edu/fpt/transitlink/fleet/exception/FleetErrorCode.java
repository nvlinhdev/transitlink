package vn.edu.fpt.transitlink.fleet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum FleetErrorCode implements ErrorCodeDefinition {
    //Depots
    DEPOT_NOT_FOUND("DEPOT_NOT_FOUND", "Depot not found", HttpStatus.NOT_FOUND),
    DEPOT_NAME_EXISTS("DEPOT_NAME_EXISTS", "Depot name already exists", HttpStatus.BAD_REQUEST),
    DEPOT_ALREADY_DELETED("DEPOT_ALREADY_DELETED", "Depot is already deleted", HttpStatus.BAD_REQUEST),
    DEPOT_NOT_DELETED("DEPOT_NOT_DELETED", "Depot is not deleted", HttpStatus.BAD_REQUEST),
    DEPOT_PLACE_NOT_FOUND("DEPOT_PLACE_NOT_FOUND", "Place not found for depot", HttpStatus.BAD_REQUEST),

    //Vehicles

    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    FleetErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

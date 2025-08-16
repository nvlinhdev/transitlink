package vn.edu.fpt.transitlink.route.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RouteErrorCode {

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

    RouteErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

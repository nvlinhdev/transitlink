package vn.edu.fpt.transitlink.shared.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND("NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT("CONFLICT", "Resource conflict", HttpStatus.CONFLICT),
    BAD_REQUEST("BAD_REQUEST", "Bad request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "Forbidden access", HttpStatus.FORBIDDEN),
    PROFILE_ALREADY_EXISTS("PROFILE_ALREADY_EXISTS", "Profile already exists", HttpStatus.CONFLICT),

    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DOWNLOAD_FAILED("FILE_DOWNLOAD_FAILED", "File download failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED("FILE_DELETE_FAILED", "File delete failed", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;


    ErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }
    public String getDefaultMessage() {
        return defaultMessage;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}

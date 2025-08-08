package vn.edu.fpt.transitlink.storage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

@Getter
public enum StorageErrorCode implements ErrorCodeDefinition {
    STORAGE_INITIALIZATION_FAILED("STORAGE_INITIALIZATION_FAILED", "Storage initialization failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "File not found", HttpStatus.NOT_FOUND),
    FILE_NOT_SUPPORTED("FILE_NOT_SUPPORTED", "File type not supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_PERMITTED("NOT_PERMITTED", "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    EXCEEDED_MAX_FILE_SIZE("EXCEEDED_MAX_FILE_SIZE", "File size exceeds the maximum limit", HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_EMPTY("FILE_EMPTY", "File must not be empty", HttpStatus.BAD_REQUEST),
    FILE_DOWNLOAD_FAILED("FILE_DOWNLOAD_FAILED", "File download failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_RETRIEVE_FAILED("FILE_RETRIEVE_FAILED", "File retrieve failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_STORE_FAILED("FILE_STORE_FAILED", "File store failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED("FILE_DELETE_FAILED", "File delete failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    StorageErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}

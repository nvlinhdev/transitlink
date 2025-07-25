package vn.edu.fpt.transitlink.storage.domain.exception;

import jakarta.servlet.http.HttpServletRequest;
import vn.edu.fpt.transitlink.shared.response.ErrorResponse;
import vn.edu.fpt.transitlink.shared.response.ErrorResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StorageModuleExceptionHandler {
    // Handle storage exceptions
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Storage Error",
                request.getRequestURI(),
                ex
        );

    }

    // Handle file not found exceptions
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.build(
                HttpStatus.NOT_FOUND,
                "File Not Found",
                request.getRequestURI(),
                ex
        );
    }

    // Handle file exceeded size exceptions
    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileSizeExceeded(FileSizeExceededException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.build(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "File Size Exceeded",
                request.getRequestURI(),
                ex
        );
    }

    // Handle invalid file exceptions
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.build(
                HttpStatus.BAD_REQUEST,
                "Invalid File",
                request.getRequestURI(),
                ex
        );
    }

}

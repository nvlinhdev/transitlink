package vn.edu.fpt.transitlink.shared.exception;

import java.time.LocalDateTime;

/**
 * Represents an error response structure for API error handling.
 * This class encapsulates the details of an error that occurred during API processing.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
    /**
     * Factory method to create an instance of ErrorResponse.
     *
     * @param status  HTTP status code
     * @param error   Error type or name
     * @param message Detailed error message
     * @param path    The request path where the error occurred
     * @return A new instance of ErrorResponse
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, LocalDateTime.now());
    }
}
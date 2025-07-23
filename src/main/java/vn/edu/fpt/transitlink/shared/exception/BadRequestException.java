package vn.edu.fpt.transitlink.shared.exception;

/**
 * Exception class representing a bad request error in the application.
 * This exception is thrown when the client sends an invalid request.
 */
public class BadRequestException extends BusinessException {
    /**
     * Constructs a BadRequestException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public BadRequestException(String message) {
        super(message);
    }
}
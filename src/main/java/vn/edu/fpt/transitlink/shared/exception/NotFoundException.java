package vn.edu.fpt.transitlink.shared.exception;

/**
 * Exception class representing a not found error in the application.
 * This exception is thrown when a requested resource cannot be found.
 */
public class NotFoundException extends BusinessException {
    /**
     * Constructs a NotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public NotFoundException(String message) {
        super(message);
    }
}
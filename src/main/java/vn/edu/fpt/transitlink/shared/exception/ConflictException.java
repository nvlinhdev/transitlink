package vn.edu.fpt.transitlink.shared.exception;

/**
 * Exception class representing a conflict error in the application.
 * This exception is thrown when there is a conflict with the current state of the resource.
 */
public class ConflictException extends BusinessException {
    /**
     * Constructs a ConflictException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ConflictException(String message) {
        super(message);
    }
}

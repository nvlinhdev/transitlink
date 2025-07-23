package vn.edu.fpt.transitlink.shared.exception;
/**
 * Base class for all business logic exceptions.
 */
public abstract class BusinessException extends RuntimeException {
    /**
     * Constructs a BusinessException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    protected BusinessException(String message) {
        super(message);
    }
}


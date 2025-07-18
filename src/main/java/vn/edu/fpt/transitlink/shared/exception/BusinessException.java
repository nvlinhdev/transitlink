package vn.edu.fpt.transitlink.shared.exception;
/**
 * Base class for all business logic exceptions.
 */
public abstract class BusinessException extends RuntimeException {
    protected BusinessException(String message) {
        super(message);
    }
}


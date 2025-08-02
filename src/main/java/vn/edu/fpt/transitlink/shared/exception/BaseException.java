package vn.edu.fpt.transitlink.shared.exception;

public class BaseException extends RuntimeException {
    private final ErrorCodeDefinition errorCode;

    public BaseException(ErrorCodeDefinition errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public  BaseException(ErrorCodeDefinition errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCodeDefinition errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCodeDefinition errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCodeDefinition getErrorCode() {
        return errorCode;
    }
}

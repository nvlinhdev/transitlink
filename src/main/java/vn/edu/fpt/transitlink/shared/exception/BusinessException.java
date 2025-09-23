package vn.edu.fpt.transitlink.shared.exception;

public class BusinessException extends RuntimeException {
    private final ErrorCodeDefinition errorCode;

    public BusinessException(ErrorCodeDefinition errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCodeDefinition errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCodeDefinition errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCodeDefinition errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCodeDefinition getErrorCode() {
        return errorCode;
    }
}

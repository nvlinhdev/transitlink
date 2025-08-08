package vn.edu.fpt.transitlink.shared.exception;

public class BusinessException extends BaseException {
    public BusinessException(ErrorCodeDefinition errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCodeDefinition errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BusinessException(ErrorCodeDefinition errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(ErrorCodeDefinition errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}


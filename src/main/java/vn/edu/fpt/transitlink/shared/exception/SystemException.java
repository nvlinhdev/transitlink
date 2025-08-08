package vn.edu.fpt.transitlink.shared.exception;

public class SystemException extends BaseException {
    public SystemException(ErrorCodeDefinition errorCode) {
        super(errorCode);
    }

    public SystemException(ErrorCodeDefinition errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public SystemException(ErrorCodeDefinition errorCode, String message) {
        super(errorCode, message);
    }

    public SystemException(ErrorCodeDefinition errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

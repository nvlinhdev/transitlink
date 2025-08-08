package vn.edu.fpt.transitlink.shared.exception;

public class ThirdPartyException extends BaseException {
    public ThirdPartyException(ErrorCodeDefinition errorCode) {
        super(errorCode);
    }

    public ThirdPartyException(ErrorCodeDefinition errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ThirdPartyException(ErrorCodeDefinition errorCode, String message) {
        super(errorCode, message);
    }

    public ThirdPartyException(ErrorCodeDefinition errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

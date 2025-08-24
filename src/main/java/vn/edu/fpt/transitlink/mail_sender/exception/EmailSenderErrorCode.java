package vn.edu.fpt.transitlink.mail_sender.exception;

import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

public enum EmailSenderErrorCode implements ErrorCodeDefinition {
    SEND_BULK_EMAIL_ERROR("SEND_BULK_EMAIL_ERROR", "Failed to send bulk emails", HttpStatus.INTERNAL_SERVER_ERROR),;
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    EmailSenderErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

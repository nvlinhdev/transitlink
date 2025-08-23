package vn.edu.fpt.transitlink.mail_sender.exception;

import org.springframework.http.HttpStatus;
import vn.edu.fpt.transitlink.shared.exception.ErrorCodeDefinition;

public enum MailSenderErrorCode implements ErrorCodeDefinition {
    MAIL_SENDER_ERROR("MAIL_SENDER_ERROR","An error occurred while sending the email",HttpStatus.INTERNAL_SERVER_ERROR);
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    MailSenderErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
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

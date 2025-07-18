package vn.edu.fpt.transitlink.shared.exception;

public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message);
    }
}
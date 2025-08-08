package vn.edu.fpt.transitlink.shared.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCodeDefinition {
    String getCode();
    String getDefaultMessage();
    HttpStatus getHttpStatus();
}

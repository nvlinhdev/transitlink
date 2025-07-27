package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

import java.util.List;

public record ErrorResponse(
        boolean success,
        String message,
        String error,
        String path,
        int statusCode,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp,
        List<ValidationError> validationErrors
) {
    // Constructor cơ bản - không có validation errors
    public ErrorResponse(String message, String error, String path, int statusCode) {
        this(false, message, error, path, statusCode, TimeUtil.now(), null);
    }

    // Constructor với validation errors
    public ErrorResponse(String message, String error, String path, int statusCode,
                         List<ValidationError> validationErrors) {
        this(false, message, error, path, statusCode, TimeUtil.now(), validationErrors);
    }
}
package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

import java.util.List;
@Schema(name = "ErrorResponse", description = "Standard error response format")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(

        @Schema(description = "Always false for errors")
        boolean success,

        @Schema(description = "User-friendly error message")
        String message,

        @Schema(description = "Technical error code")
        String error,

        @Schema(description = "The URI path where the error occurred")
        String path,

        @Schema(description = "HTTP status code")
        int statusCode,

        @Schema(description = "Timestamp when the error occurred")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp,

        @Schema(description = "List of validation errors (if applicable)")
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
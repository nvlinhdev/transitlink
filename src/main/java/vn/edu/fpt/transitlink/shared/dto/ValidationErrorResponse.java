package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

import java.util.List;

@Schema(name = "ValidationErrorResponse", description = "Validation error response including field-level details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationErrorResponse(
        @Schema(description = "Always false for errors", example = "false")
        boolean success,
        @Schema(description = "Summary validation message", example = "Validation failed: email: must not be blank; age: must be greater than 0")
        String message,
        @Schema(description = "Technical error code (exception simple name)", example = "MethodArgumentNotValidException")
        String error,
        @Schema(description = "Request path where the error occurred", example = "/api/users")
        String path,
        @Schema(description = "Timestamp when the error occurred", example = "2025-01-01 10:00:00 UTC")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp,
        @Schema(description = "List of field-level validation errors")
        List<ValidationError> validationErrors
) {
    public ValidationErrorResponse(String message, String error, String path,
                                   List<ValidationError> validationErrors) {
        this(false, message, error, path, TimeUtil.now(), validationErrors);
    }
}
package vn.edu.fpt.transitlink.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationError", description = "Details of a field-level validation error")
public record ValidationError(
        @Schema(description = "Field name that failed validation", example = "password")
        String field,
        @Schema(description = "Validation message", example = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String message,
        @Schema(description = "Rejected value", example = "123456")
        Object rejectedValue
) {}
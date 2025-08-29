package vn.edu.fpt.transitlink.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationError", description = "Details of a field-level validation error")
public record ValidationError(
        @Schema(description = "Field name that failed validation", example = "email")
        String field,
        @Schema(description = "Validation message", example = "must not be blank")
        String message,
        @Schema(description = "Rejected value", example = "")
        Object rejectedValue
) {}
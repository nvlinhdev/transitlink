package vn.edu.fpt.transitlink.shared.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationError", description = "Details of a field-level validation error")
public record ValidationError(

        @Schema(description = "Field name that failed validation")
        String field,

        @Schema(description = "Error message related to the field")
        String message,

        @Schema(description = "The invalid value that was rejected")
        Object rejectedValue
) {}
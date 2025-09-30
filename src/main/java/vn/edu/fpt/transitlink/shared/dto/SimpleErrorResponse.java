package vn.edu.fpt.transitlink.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.shared.util.TimeUtil;

@Schema(name = "SimpleErrorResponse", description = "Standard error response without field-level validation details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimpleErrorResponse(
        @Schema(description = "Always false for errors", example = "false")
        boolean success,
        @Schema(description = "User-friendly error message", example = "Access denied - insufficient permissions")
        String message,
        @Schema(description = "Technical error code or exception simple name", example = "AccessDeniedException")
        String error,
        @Schema(description = "Request path where the error occurred", example = "/api/admin/users")
        String path,
        @Schema(description = "Timestamp when the error occurred", example = "2025-01-01 10:00:00 UTC")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss 'UTC'")
        String timestamp
) {
    public SimpleErrorResponse(String message, String error, String path) {
        this(false, message, error, path, TimeUtil.now());
    }
}
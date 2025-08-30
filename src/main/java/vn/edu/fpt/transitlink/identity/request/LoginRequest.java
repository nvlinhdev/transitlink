package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(example = "user@example.com", description = "Email address or username used for login")
        @NotBlank(message = "Identifier cannot be blank")
        @Size(max = 100, message = "Identifier must not exceed 100 characters")
        String identifier,

        @Schema(example = "Password123!", description = "User's password")
        @NotBlank(message = "Password cannot be blank")
        String password
) {
}

package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationRequest(
        @Schema(example = "user@example.com", description = "Email address to resend verification code to")
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {}
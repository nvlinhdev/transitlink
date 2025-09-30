package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InitiateEmailChangeRequest(
    @Schema(example = "new.email@example.com", description = "New email address to change to")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String newEmail
) {}

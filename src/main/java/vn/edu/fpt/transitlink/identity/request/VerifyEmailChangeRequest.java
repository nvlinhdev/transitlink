package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailChangeRequest(
    @Schema(example = "new.email@example.com", description = "New email address that was requested to change to")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String newEmail,

    @Schema(example = "123456", description = "6-digit OTP verification code sent to the new email")
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit number")
    String otp
) {}

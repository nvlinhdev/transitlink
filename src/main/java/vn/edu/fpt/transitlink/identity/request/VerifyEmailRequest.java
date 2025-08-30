package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import vn.edu.fpt.transitlink.identity.enumeration.VerifyMethod;

public record VerifyEmailRequest(
        @Schema(example = "OTP", description = "Verification method (OTP or TOKEN)")
        @NotNull(message = "Method is required")
        VerifyMethod method,

        @Schema(example = "user@example.com", description = "Email address to verify")
        @Email(message = "Email should be valid")
        String email,

        @Schema(example = "123456", description = "6-digit OTP code")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
        String otp,

        @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "Verification token")
        @Size(max = 128, message = "Token must not exceed 128 characters")
        String token
) {}

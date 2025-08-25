package vn.edu.fpt.transitlink.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import vn.edu.fpt.transitlink.identity.enumeration.VerifyMethod;

public record VerifyEmailRequest(
        @NotNull(message = "Method is required")
        VerifyMethod method,
        @Email(message = "Email should be valid")
        String email,
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
        String otp,
        @Size(max = 128, message = "Token must not exceed 128 characters")
        String token
) {}

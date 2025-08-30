package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import vn.edu.fpt.transitlink.identity.enumeration.VerifyMethod;

public record ResetPasswordRequest(
        @Schema(example = "OTP", description = "Method used for verification (OTP or TOKEN)")
        @NotNull(message = "Verify method cannot be blank")
        VerifyMethod method,

        @Schema(example = "user@example.com", description = "Email address associated with the account")
        @Email(message = "Email format is invalid")
        String email,

        @Schema(example = "123456", description = "6-digit OTP code received via email")
        String otp,

        @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "Token received via email for verification")
        String token,

        @Schema(example = "NewPassword@123", description = "New password to set for the account")
        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
        )
        String newPassword
) {}

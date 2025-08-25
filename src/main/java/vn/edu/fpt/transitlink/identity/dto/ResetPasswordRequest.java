package vn.edu.fpt.transitlink.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import vn.edu.fpt.transitlink.identity.enumeration.VerifyMethod;

public record ResetPasswordRequest(
        @NotNull(message = "Verify method cannot be blank")
        VerifyMethod method,
        String email,
        String otp,
        String token,
        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
        )
        String newPassword
) {}

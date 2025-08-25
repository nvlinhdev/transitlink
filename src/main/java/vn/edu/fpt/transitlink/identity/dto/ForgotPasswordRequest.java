package vn.edu.fpt.transitlink.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email
) {}

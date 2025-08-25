package vn.edu.fpt.transitlink.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Identifier cannot be blank")
        @Size(max = 100, message = "Identifier must not exceed 100 characters")
        String identifier,
        @NotBlank(message = "Password cannot be blank")
        String password
) {
}

package vn.edu.fpt.transitlink.identity.request;

import jakarta.validation.constraints.NotBlank;

public record SetPasswordRequest(
    @NotBlank(message = "New password must not be blank")
    String newPassword
) {}


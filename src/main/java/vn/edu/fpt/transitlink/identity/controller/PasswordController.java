package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.request.ChangePasswordRequest;
import vn.edu.fpt.transitlink.identity.request.ForgotPasswordRequest;
import vn.edu.fpt.transitlink.identity.request.ResetPasswordRequest;
import vn.edu.fpt.transitlink.identity.request.SetPasswordRequest;
import vn.edu.fpt.transitlink.identity.service.PasswordService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/identity/auth/password")
@Tag(name = "Password Management", description = "APIs for password reset and management")
public class PasswordController {
    private final PasswordService passwordService;

    @Operation(summary = "Forgot password request",
            description = "Send reset password link/code to user's email"
    )
    @PostMapping("/forgot")
    public ResponseEntity<StandardResponse<Void>> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.sendResetPasswordEmail(request);
        return ResponseEntity.ok(StandardResponse.success("Password reset email sent successfully", null));
    }

    @Operation(summary = "Reset password",
            description = "Reset user's password using reset token"
    )
    @PostMapping("/reset")
    public ResponseEntity<StandardResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok(StandardResponse.success("Password reset successfully", null));
    }

    @Operation(summary = "Change password",
            description = "Change user's password by providing old and new password"
    )
    @PutMapping("/change")
    public ResponseEntity<StandardResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(principal.getEmail(), request);
        return ResponseEntity.ok(StandardResponse.success("Password changed successfully", null));
    }

    @Operation(summary = "Set password",
            description = "Set a password for accounts that do not have one (e.g., registered via Google)"
    )
    @PostMapping("/set")
    public ResponseEntity<StandardResponse<Void>> setPassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody SetPasswordRequest request) {
        passwordService.setPassword(principal.getEmail(), request);
        return ResponseEntity.ok(StandardResponse.success("Password set successfully", null));
    }


}

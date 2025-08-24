package vn.edu.fpt.transitlink.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.auth.dto.ForgotPasswordRequest;
import vn.edu.fpt.transitlink.auth.dto.ResetPasswordRequest;
import vn.edu.fpt.transitlink.auth.service.PasswordService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/password")
class PasswordController {
    private final PasswordService passwordService;

    @Operation(summary = "Request password reset",
            description = "Send reset password link/code to user's email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset email sent successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/request")
    public ResponseEntity<StandardResponse<Void>> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.sendResetPasswordEmail(request);
        return ResponseEntity.ok(StandardResponse.noContent("Password reset email sent successfully"));
    }

    @Operation(summary = "Reset password",
            description = "Reset user's password using reset token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired reset token",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/reset")
    public ResponseEntity<StandardResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok(StandardResponse.noContent("Password reset successfully"));
    }
}

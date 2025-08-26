package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            description = "Send reset password link/code to user's email",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    schema = @Schema(implementation = ForgotPasswordRequest.class),
                    examples = @ExampleObject(
                        name = "ForgotPasswordRequest Example",
                        value = "{\"email\": \"user@example.com\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset email sent successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Password reset email sent successfully\", \"data\": null}"
                                )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Invalid request data\", \"data\": null}"
                                )
                            )
                    )
            })
    @PostMapping("/forgot")
    public ResponseEntity<StandardResponse<Void>> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.sendResetPasswordEmail(request);
        return ResponseEntity.ok(StandardResponse.success("Password reset email sent successfully", null));
    }

    @Operation(summary = "Reset password",
            description = "Reset user's password using reset token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    schema = @Schema(implementation = ResetPasswordRequest.class),
                    examples = @ExampleObject(
                        name = "ResetPasswordRequest Example",
                        value = "{\"email\": \"user@example.com\", \"token\": \"abcdef\", \"newPassword\": \"NewStrongPassword123\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Password reset successfully\", \"data\": null}"
                                )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired reset token",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Invalid or expired reset token\", \"data\": null}"
                                )
                            )
                    )
            })
    @PostMapping("/reset")
    public ResponseEntity<StandardResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok(StandardResponse.success("Password reset successfully", null));
    }

    @Operation(summary = "Change password",
            description = "Change user's password by providing old and new password",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    schema = @Schema(implementation = ChangePasswordRequest.class),
                    examples = @ExampleObject(
                        name = "ChangePasswordRequest Example",
                        value = "{\"oldPassword\": \"OldPassword123!\", \"newPassword\": \"NewStrongPassword123!\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Password changed successfully\", \"data\": null}"
                                )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data or old password",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Invalid request data or old password\", \"data\": null}"
                                )
                            )
                    )
            })
    @PutMapping("/change")
    public ResponseEntity<StandardResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(principal.getEmail(), request);
        return ResponseEntity.ok(StandardResponse.success("Password changed successfully", null));
    }

    @Operation(summary = "Set password",
            description = "Set a password for accounts that do not have one (e.g., registered via Google)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    schema = @Schema(implementation = SetPasswordRequest.class),
                    examples = @ExampleObject(
                        name = "SetPasswordRequest Example",
                        value = "{\"newPassword\": \"NewStrongPassword123!\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password set successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Password set successfully\", \"data\": null}"
                                )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Account already has a password or invalid request",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Account already has a password or invalid request\", \"data\": null}"
                                )
                            )
                    )
            })
    @PostMapping("/set")
    public ResponseEntity<StandardResponse<Void>> setPassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody SetPasswordRequest request) {
        passwordService.setPassword(principal.getEmail(), request);
        return ResponseEntity.ok(StandardResponse.success("Password set successfully", null));
    }


}

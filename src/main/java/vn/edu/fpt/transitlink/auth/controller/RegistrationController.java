package vn.edu.fpt.transitlink.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.auth.dto.RegisterRequest;
import vn.edu.fpt.transitlink.auth.dto.ResendVerificationRequest;
import vn.edu.fpt.transitlink.auth.dto.VerifyEmailRequest;
import vn.edu.fpt.transitlink.auth.service.RegistrationService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/registration")
class RegistrationController {
    private final RegistrationService registrationService;

    @Operation(summary = "Register a new user",
            description = "Registers a new user with email/phone and password",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping
    public ResponseEntity<StandardResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.noContent("User registered successfully"));
    }

    @Operation(summary = "Verify email via code",
            description = "Verify user's email by the code sent to their email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email verified successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired verification code",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/verify-email")
    public ResponseEntity<StandardResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        registrationService.verifyEmail(request);
        return ResponseEntity.ok(StandardResponse.noContent("Email verified successfully"));
    }

    @Operation(summary = "Resend verification email",
            description = "Resends email verification code to user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Verification email sent successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/resend")
    public ResponseEntity<StandardResponse<Void>> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request) {
        registrationService.resendVerificationEmail(request);
        return ResponseEntity.ok(StandardResponse.noContent("Verification email sent successfully"));
    }

}

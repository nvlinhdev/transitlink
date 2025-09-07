package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.request.RegisterRequest;
import vn.edu.fpt.transitlink.identity.request.ResendVerificationRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailRequest;
import vn.edu.fpt.transitlink.identity.service.RegistrationService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/identity/registration")
@Tag(name = "User Registration", description = "APIs for user registration and email verification")
class RegistrationController {
    private final RegistrationService registrationService;

    @Operation(summary = "Register a new user",
            description = "Registers a new user with email/phone and password"
    )
    @PostMapping
    public ResponseEntity<StandardResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("User registered successfully", null));
    }

    @Operation(summary = "Verify email via code",
            description = "Verify user's email by the code sent to their email"
    )
    @PostMapping("/verify-email")
    public ResponseEntity<StandardResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        registrationService.verifyEmail(request);
        return ResponseEntity.ok(StandardResponse.success("Email verified successfully", null));
    }

    @Operation(summary = "Resend verification email",
            description = "Resends email verification code to user"
    )
    @PostMapping("/resend")
    public ResponseEntity<StandardResponse<Void>> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request) {
        registrationService.resendVerificationEmail(request);
        return ResponseEntity.ok(StandardResponse.success("Verification email sent successfully", null));
    }

}

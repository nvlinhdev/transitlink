package vn.edu.fpt.transitlink.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.auth.dto.*;
import vn.edu.fpt.transitlink.auth.service.AuthService;
import vn.edu.fpt.transitlink.auth.service.EmailVerificationService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and email verification")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "Register a new user",
            description = "Registers a new user with email/phone and password",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED.value())
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
        emailVerificationService.verifyEmail(request);
        return ResponseEntity.ok(StandardResponse.noContent("Email verified successfully"));
    }

    @Operation(summary = "Verify email via URL token",
            description = "Verify user's email by token received in URL",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email verified successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired token",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/verify-email-url")
    public ResponseEntity<StandardResponse<Void>> verifyEmailByUrl(@RequestParam String token) {
        emailVerificationService.verifyEmailByUrl(token);
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
    @PostMapping("/resend-verification")
    public ResponseEntity<StandardResponse<Void>> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request) {
        emailVerificationService.resendVerificationEmail(request);
        return ResponseEntity.ok(StandardResponse.noContent("Verification email sent successfully"));
    }

    @Operation(summary = "Login user",
            description = "Authenticate user with email/phone and password and return accessToken + refreshToken",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/login")
    public ResponseEntity<StandardResponse<TokenData>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenData tokenData = authService.login(loginRequest);
        return ResponseEntity.ok(StandardResponse.success("User logged in successfully", tokenData));
    }

    @Operation(summary = "Refresh JWT token",
            description = "Refresh the access token using a valid refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<TokenData>> refreshToken(@Valid @RequestBody RefreshRequest request) {
        TokenData tokenData = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(StandardResponse.success("Token refreshed successfully", tokenData));
    }

    @Operation(summary = "Logout user",
            description = "Invalidate the refresh token and logout user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid refresh token",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/logout")
    public ResponseEntity<StandardResponse<Void>> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(StandardResponse.noContent("User logged out successfully"));
    }

    @Operation(summary = "Login via Google for mobile",
            description = "Authenticate mobile user via Google ID Token and return internal JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid Google ID Token",
                            content = @Content(schema = @Schema(implementation = StandardResponse.class)))
            })
    @PostMapping("/google/mobile")
    public ResponseEntity<StandardResponse<TokenData>> loginWithGoogleMobile(@RequestBody GoogleTokenRequest request) {
        TokenData tokenData = authService.loginWithGoogleMobile(request.idToken());
        return ResponseEntity.ok(StandardResponse.success("User logged in via Google", tokenData));
    }

}

package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            description = "Registers a new user with email/phone and password",
            requestBody = @RequestBody(
                content = @Content(
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(
                        name = "RegisterRequest Example",
                        value = "{\"email\": \"user@example.com\", \"password\": \"StrongPassword123\", \"phone\": \"0123456789\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"User registered successfully\", \"data\": null}"
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
    @PostMapping
    public ResponseEntity<StandardResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("User registered successfully", null));
    }

    @Operation(summary = "Verify email via code",
            description = "Verify user's email by the code sent to their email",
            requestBody = @RequestBody(
                content = @Content(
                    schema = @Schema(implementation = VerifyEmailRequest.class),
                    examples = @ExampleObject(
                        name = "VerifyEmailRequest Example",
                        value = "{\"email\": \"user@example.com\", \"code\": \"123456\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email verified successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Email verified successfully\", \"data\": null}"
                                )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired verification code",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Invalid or expired verification code\", \"data\": null}"
                                )
                            )
                    )
            })
    @PostMapping("/verify-email")
    public ResponseEntity<StandardResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        registrationService.verifyEmail(request);
        return ResponseEntity.ok(StandardResponse.success("Email verified successfully", null));
    }

    @Operation(summary = "Resend verification email",
            description = "Resends email verification code to user",
            requestBody = @RequestBody(
                content = @Content(
                    schema = @Schema(implementation = ResendVerificationRequest.class),
                    examples = @ExampleObject(
                        name = "ResendVerificationRequest Example",
                        value = "{\"email\": \"user@example.com\"}"
                    )
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Verification email sent successfully",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Verification email sent successfully\", \"data\": null}"
                                )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(
                                schema = @Schema(implementation = StandardResponse.class),
                                examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Invalid request\", \"data\": null}"
                                )
                            )
                    )
            })
    @PostMapping("/resend")
    public ResponseEntity<StandardResponse<Void>> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request) {
        registrationService.resendVerificationEmail(request);
        return ResponseEntity.ok(StandardResponse.success("Verification email sent successfully", null));
    }

}

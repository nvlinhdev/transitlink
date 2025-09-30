package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.dto.*;
import vn.edu.fpt.transitlink.identity.request.GoogleTokenRequest;
import vn.edu.fpt.transitlink.identity.request.LoginRequest;
import vn.edu.fpt.transitlink.identity.request.RefreshTokenRequest;
import vn.edu.fpt.transitlink.identity.service.AuthService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@AllArgsConstructor
@RestController
@RequestMapping("/api/identity/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and email verification")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login user",
            description = "Authenticate user with email/phone and password and return accessToken + refreshToken"
    )
    @PostMapping("/login")
    public ResponseEntity<StandardResponse<TokenData>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenData tokenData = authService.login(loginRequest);
        return ResponseEntity.ok(StandardResponse.success("User logged in successfully", tokenData));
    }

    @Operation(summary = "Refresh JWT token",
            description = "Refresh the access token using a valid refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<TokenData>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenData tokenData = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(StandardResponse.success("Token refreshed successfully", tokenData));
    }

    @Operation(summary = "Logout user",
            description = "Invalidate the refresh token and logout user"
    )
    @DeleteMapping("/logout")
    public ResponseEntity<StandardResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(StandardResponse.success("User logged out successfully", null));
    }

    @Operation(summary = "Login via Google for mobile",
            description = "Authenticate mobile user via Google ID Token and return internal JWT"
    )
    @PostMapping("/google/mobile")
    public ResponseEntity<StandardResponse<TokenData>> loginWithGoogleMobile(@RequestBody GoogleTokenRequest request) {
        TokenData tokenData = authService.loginWithGoogleMobile(request.idToken());
        return ResponseEntity.ok(StandardResponse.success("User logged in via Google", tokenData));
    }

}

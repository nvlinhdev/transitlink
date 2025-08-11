package vn.edu.fpt.transitlink.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.service.ProfileService;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.shared.dto.ErrorResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.util.RequestContextUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;


@Tag(name = "Profile", description = "Operations related to user profiles")
@SecurityRequirement(name = "keycloak")
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create a new user profile",
            description = "Creates a new user profile for the authenticated user.",
            tags = {"Profile"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Profile created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardResponse.class),
                                    examples = @ExampleObject(
                                            name = "CreateProfileResponse",
                                            summary = "Example response for creating a profile",
                                            value = """
                                                    {
                                                        "success": true,
                                                        "message": "Profile created successfully",
                                                        "data": {
                                                            "id": "123e4567-e89b-12d3-a456-426614174000",
                                                            "accountId": "123e4567-e89b-12d3-a456-426614174001",
                                                            "firstName": "John",
                                                            "lastName": "Doe",
                                                            "phoneNumber": "+1234567890",
                                                            "email": "jondoe@gmail.com",
                                                            "gender": "MALE",
                                                            "zaloPhoneNumber": "+1234567890",
                                                            "avatarUrl": "https://example.com/avatar.jpg"
                                                        },
                                                        "timestamp": "2023-10-01T12:00:00Z",
                                                        "statusCode": 201
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Profile already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "ProfileAlreadyExistsError",
                                            summary = "Error response when profile already exists",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "Profile already exists for account: 123e4567-e89b-12d3-a456-426614174001",
                                                        "errorCode": "PROFILE_ALREADY_EXISTS",
                                                        "timestamp": "2023-10-01T12:00:00Z",
                                                        "statusCode": 409
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Phone number not found in Firebase token / Token already used / Invalid Firebase token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "PhoneNumberNotInTokenError",
                                                    summary = "Phone number not found in Firebase token",
                                                    value = """
                                                                {
                                                                    "success": false,
                                                                    "message": "Phone number not found in Firebase token",
                                                                    "errorCode": "PHONE_NUMBER_NOT_IN_TOKEN",
                                                                    "timestamp": "2023-10-01T12:00:00Z",
                                                                    "statusCode": 400
                                                                }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "PhoneVerificationAlreadyUsedError",
                                                    summary = "Phone verification token already used",
                                                    value = """
                                                                {
                                                                    "success": false,
                                                                    "message": "This phone verification token has already been used",
                                                                    "errorCode": "PHONE_VERIFICATION_ALREADY_USED",
                                                                    "timestamp": "2023-10-01T12:00:00Z",
                                                                    "statusCode": 400
                                                                }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "PhoneVerificationTokenInvalidError",
                                                    summary = "Invalid Firebase phone verification token",
                                                    value = """
                                                                {
                                                                    "success": false,
                                                                    "message": "Invalid Firebase phone verification token",
                                                                    "errorCode": "PHONE_VERIFICATION_TOKEN_INVALID",
                                                                    "timestamp": "2023-10-01T12:00:00Z",
                                                                    "statusCode": 400
                                                                }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Token expired or verification failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "PhoneVerificationTokenExpiredError",
                                                    summary = "ID token expired for verification purposes",
                                                    value = """
                                                                {
                                                                    "success": false,
                                                                    "message": "ID token expired for verification purposes",
                                                                    "errorCode": "PHONE_VERIFICATION_TOKEN_EXPIRED",
                                                                    "timestamp": "2023-10-01T12:00:00Z",
                                                                    "statusCode": 401
                                                                }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "PhoneVerificationFailedError",
                                                    summary = "Firebase token verification failed",
                                                    value = """
                                                                {
                                                                    "success": false,
                                                                    "message": "Firebase token verification failed",
                                                                    "errorCode": "PHONE_VERIFICATION_FAILED",
                                                                    "timestamp": "2023-10-01T12:00:00Z",
                                                                    "statusCode": 401
                                                                }
                                                            """
                                            )
                                    }
                            )
                    ),
            }
    )
    @PostMapping
    public ResponseEntity<StandardResponse<UserProfileDTO>> createProfile(
            @Valid @RequestBody CreateProfileRequest request,
            Principal principal
    ) {
        UUID accountId = RequestContextUtil.getAccountId(principal);
        UserProfileDTO response = service.createProfile(accountId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponse.created(response));
    }

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserProfileDTO>> getProfile(Principal principal) {
        UUID accountId = RequestContextUtil.getAccountId(principal);
        UserProfileDTO profile = service.getProfile(accountId);
        return ResponseEntity.ok(StandardResponse.success(profile));
    }
}

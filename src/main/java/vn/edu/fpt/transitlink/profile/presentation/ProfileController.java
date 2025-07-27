package vn.edu.fpt.transitlink.profile.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import vn.edu.fpt.transitlink.profile.presentation.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.application.ProfileService;
import vn.edu.fpt.transitlink.profile.presentation.dto.UserProfileDTO;
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

    @PostMapping
    public ResponseEntity<StandardResponse<UserProfileDTO>> createProfile(@Valid @RequestBody CreateProfileRequest request,
                                                                          Principal principal) {
        UUID accountId = RequestContextUtil.getAccountId(principal);
        UserProfileDTO response = service.createProfile(accountId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponse.created(response));
    }
}

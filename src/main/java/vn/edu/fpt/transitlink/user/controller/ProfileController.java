package vn.edu.fpt.transitlink.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.user.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.user.dto.UpdateBasicInfoRequest;
import vn.edu.fpt.transitlink.user.dto.UpdatePhoneNumberRequest;
import vn.edu.fpt.transitlink.user.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.user.service.ProfileService;

import java.security.Principal;

@RequestMapping("/api/user/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserProfileDTO>> getMyProfile(Principal principal) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<UserProfileDTO>> getProfileById(
            @PathVariable("id") String id,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<UserProfileDTO>> searchProfile(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping
    public ResponseEntity<StandardResponse<UserProfileDTO>> createProfile(
            @Valid @RequestBody CreateProfileRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/me/basic-info")
    public ResponseEntity<StandardResponse<UserProfileDTO>> updateBasicInfo(
            @Valid @RequestBody UpdateBasicInfoRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/me/phone-number")
    public ResponseEntity<StandardResponse<UserProfileDTO>> updatePhoneNumber(
            @Valid @RequestBody UpdatePhoneNumberRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<StandardResponse<Void>> deleteMyProfile(Principal principal) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


}

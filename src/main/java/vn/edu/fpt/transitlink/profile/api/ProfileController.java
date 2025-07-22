package vn.edu.fpt.transitlink.profile.api;

import vn.edu.fpt.transitlink.profile.api.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.api.dto.UserProfileResponse;
import vn.edu.fpt.transitlink.profile.application.ProfileService;
import vn.edu.fpt.transitlink.shared.dto.ApiResponse;
import vn.edu.fpt.transitlink.shared.util.RequestContextUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request,
                                                          Principal principal) {
        UUID accountId = RequestContextUtil.getAccountId(principal);
        UserProfileResponse response = service.createProfile(accountId, request);
        return ApiResponse.success(response);
    }
}

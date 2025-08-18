package vn.edu.fpt.transitlink.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.user.dto.DriverDTO;
import vn.edu.fpt.transitlink.user.dto.PassengerDTO;
import vn.edu.fpt.transitlink.user.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.user.dto.UpdateBasicInfoRequest;
import vn.edu.fpt.transitlink.user.dto.UpdatePhoneNumberRequest;
import vn.edu.fpt.transitlink.user.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.user.service.UserService;

import java.security.Principal;

@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    //profile
    @GetMapping("/profiles/me")
    public ResponseEntity<StandardResponse<UserProfileDTO>> getMyProfile(Principal principal) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<StandardResponse<UserProfileDTO>> getProfileById(
            @PathVariable("id") String id,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/profiles/search")
    public ResponseEntity<PaginatedResponse<UserProfileDTO>> searchProfile(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/profiles")
    public ResponseEntity<StandardResponse<UserProfileDTO>> createProfile(
            @Valid @RequestBody CreateProfileRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/profiles/me/basic-info")
    public ResponseEntity<StandardResponse<UserProfileDTO>> updateBasicInfo(
            @Valid @RequestBody UpdateBasicInfoRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/profiles/me/phone-number")
    public ResponseEntity<StandardResponse<UserProfileDTO>> updatePhoneNumber(
            @Valid @RequestBody UpdatePhoneNumberRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/profiles/me")
    public ResponseEntity<StandardResponse<Void>> deleteMyProfile(Principal principal) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //driver
    @PostMapping("/drivers")
    public ResponseEntity<StandardResponse<DriverDTO>> enterDriverData(
            // @Valid @RequestBody EnterDriverDataRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/drivers/import")
    public ResponseEntity<StandardResponse<Void>> importDriverData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/drivers")
    public ResponseEntity<StandardResponse<Void>> viewDriverList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteDriverData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //passenger
    @PostMapping("/passengers")
    public ResponseEntity<StandardResponse<PassengerDTO>> enterPassengerData(
            // @Valid @RequestBody EnterPassengerDataRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/passengers/import")
    public ResponseEntity<StandardResponse<Void>> importPassengerData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/passengers")
    public ResponseEntity<StandardResponse<Void>> viewPassengerList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/passengers/{id}")
    public ResponseEntity<StandardResponse<Void>> deletePassengerData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

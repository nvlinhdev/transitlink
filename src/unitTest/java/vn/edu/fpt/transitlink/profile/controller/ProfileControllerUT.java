package vn.edu.fpt.transitlink.profile.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.profile.service.ProfileService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.util.RequestContextUtil;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

public class ProfileControllerUT {
    @Mock
    private ProfileService profileService;
    @Mock
    private Principal principal;
    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProfile_Success() {
        UUID accountId = UUID.randomUUID();
        CreateProfileRequest request = new CreateProfileRequest(
                "Linh",
                "Nguyen",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png",
                "eyJhbGciOiJSUzI1..."
        );
        UserProfileDTO userProfileDTO = new UserProfileDTO(
                UUID.randomUUID(),
                accountId,
                "Linh",
                "Nguyen",
                "+84987654321",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png"
        );
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(principal)).thenReturn(accountId);
            when(profileService.createProfile(accountId, request)).thenReturn(userProfileDTO);

            ResponseEntity<StandardResponse<UserProfileDTO>> response = profileController.createProfile(request, principal);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().success());
            assertEquals(userProfileDTO, response.getBody().data());
        }
    }

    @Test
    void testCreateProfile_ProfileAlreadyExists() {
        UUID accountId = UUID.randomUUID();
        CreateProfileRequest request = new CreateProfileRequest(
                "Linh",
                "Nguyen",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png",
                "eyJhbGciOiJSUzI1..."
        );
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(principal)).thenReturn(accountId);
            when(profileService.createProfile(accountId, request)).thenThrow(new RuntimeException("Profile already exists"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                profileController.createProfile(request, principal);
            });
            assertEquals("Profile already exists", exception.getMessage());
        }
    }

    @Test
    void testCreateProfile_InvalidToken() {
        CreateProfileRequest request = new CreateProfileRequest(
                "Linh",
                "Nguyen",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png",
                "invalid-token"
        );
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(principal)).thenThrow(new SecurityException("Invalid token"));

            Exception exception = assertThrows(SecurityException.class, () -> {
                profileController.createProfile(request, principal);
            });
            assertEquals("Invalid token", exception.getMessage());
        }
    }

    @Test
    void testCreateProfile_NullRequest() {
        UUID accountId = UUID.randomUUID();
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(principal)).thenReturn(accountId);
        }
    }

    @Test
    void testCreateProfile_PrincipalNull() {
        CreateProfileRequest request = new CreateProfileRequest(
                "Linh",
                "Nguyen",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png",
                "eyJhbGciOiJSUzI1..."
        );
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(null)).thenThrow(new SecurityException("Principal is null"));
            Exception exception = assertThrows(SecurityException.class, () -> {
                profileController.createProfile(request, null);
            });
            assertEquals("Principal is null", exception.getMessage());
        }
    }

    @Test
    void testCreateProfile_ServiceThrowsUnexpectedException() {
        UUID accountId = UUID.randomUUID();
        CreateProfileRequest request = new CreateProfileRequest(
                "Linh",
                "Nguyen",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png",
                "eyJhbGciOiJSUzI1..."
        );
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(principal)).thenReturn(accountId);
            when(profileService.createProfile(accountId, request)).thenThrow(new IllegalStateException("Unexpected error"));
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                profileController.createProfile(request, principal);
            });
            assertEquals("Unexpected error", exception.getMessage());
        }
    }

    @Test
    void testCreateProfile_ServiceReturnsNull() {
        UUID accountId = UUID.randomUUID();
        CreateProfileRequest request = new CreateProfileRequest(
                "Linh",
                "Nguyen",
                vn.edu.fpt.transitlink.profile.entity.Gender.MALE,
                java.time.LocalDate.of(2000, 1, 15),
                "0912345678",
                "https://cdn.example.com/avatar.png",
                "eyJhbGciOiJSUzI1..."
        );
        try (MockedStatic<RequestContextUtil> mocked = mockStatic(RequestContextUtil.class)) {
            mocked.when(() -> RequestContextUtil.getAccountId(principal)).thenReturn(accountId);
            when(profileService.createProfile(accountId, request)).thenReturn(null);
            ResponseEntity<?> response = profileController.createProfile(request, principal);
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNull(((StandardResponse<?>) response.getBody()).data());
        }
    }

    // TODO: Add more tests for other error cases (if any)
}

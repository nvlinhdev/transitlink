package vn.edu.fpt.transitlink.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.dto.NotificationTokenDTO;
import vn.edu.fpt.transitlink.notification.request.RegisterTokenRequest;
import vn.edu.fpt.transitlink.notification.service.NotificationService;
import vn.edu.fpt.transitlink.notification.service.NotificationTokenService;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management APIs")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationTokenService tokenService;

    // ==== USER ENDPOINTS (dùng trong client) ====

    @GetMapping("/me")
    public ResponseEntity<PaginatedResponse<NotificationDTO>> getMyNotifications(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByAccount(customUserPrincipal.getId(), page, size);
        Long totalItems = notificationService.countNotificationsByAccount(customUserPrincipal.getId());
        PaginatedResponse<NotificationDTO> response = new PaginatedResponse<>(notifications, page, size, totalItems);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/read/{notificationId}")
    public ResponseEntity<StandardResponse<Void>> markAsRead(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PathVariable UUID notificationId
    ) {
        notificationService.markAsRead(customUserPrincipal.getId(), notificationId);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

    @DeleteMapping("/me/{notificationId}")
    public ResponseEntity<StandardResponse<Void>> deleteNotification(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PathVariable UUID notificationId
    ) {
        notificationService.deleteNotification(customUserPrincipal.getId(), notificationId);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

    @GetMapping("/me/unread/count")
    public ResponseEntity<StandardResponse<Long>> countUnreadNotifications(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ) {
        Long count = notificationService.countNotificationNotRead(customUserPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(count));
    }

    // ==== TOKEN ENDPOINTS ====

    @PostMapping("/tokens")
    public ResponseEntity<StandardResponse<NotificationTokenDTO>> registerToken(
            @Valid @RequestBody RegisterTokenRequest request,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ) {
        NotificationTokenDTO saved = tokenService.registerToken(customUserPrincipal.getId(), request);
        return ResponseEntity.ok(StandardResponse.success(saved));
    }

    @DeleteMapping("/tokens/{token}")
    public ResponseEntity<StandardResponse<Void>> deactivateToken(@PathVariable String token) {
        tokenService.deactivateToken(token);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

}



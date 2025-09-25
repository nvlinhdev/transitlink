package vn.edu.fpt.transitlink.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.request.CreateNotificationRequest;
import vn.edu.fpt.transitlink.notification.request.SendAtRequest;
import vn.edu.fpt.transitlink.notification.service.NotificationService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/test")
@RequiredArgsConstructor
@Tag(name = "Only Test Sending Notification Controller (Not Implement)", description = "Endpoints for testing sending notifications")
public class TestSendingNotificationController {
    private final NotificationService notificationService;

    // ==== ADMIN/TEST ENDPOINTS ====

    @PostMapping
    public ResponseEntity<StandardResponse<NotificationDTO>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request
    ) {
        return ResponseEntity.ok(StandardResponse.success(notificationService.createNotification(request)));
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<StandardResponse<NotificationDTO>> updateNotification(
            @PathVariable UUID notificationId,
            @Valid @RequestBody CreateNotificationRequest request
    ) {
        return ResponseEntity.ok(StandardResponse.success(notificationService.updateNotification(notificationId, request)));
    }

    @PostMapping("/send/mobile/{notificationId}")
    public ResponseEntity<StandardResponse<Void>> sendNotificationToMobile(@PathVariable UUID notificationId) {
        notificationService.sendNotificationToMobile(notificationId);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

    @PostMapping("/send/web/{notificationId}")
    public ResponseEntity<StandardResponse<Void>> sendNotificationToWeb(@PathVariable UUID notificationId) {
        notificationService.sendNotificationToWeb(notificationId);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

    @PostMapping("/{notificationId}/send-to-topic/{topic}")
    public ResponseEntity<StandardResponse<Void>> sendToTopic(
            @PathVariable UUID notificationId,
            @PathVariable String topic
    ) {
        notificationService.sendToTopic(topic, notificationId);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

    @PostMapping("/schedule")
    public ResponseEntity<StandardResponse<Void>> sendAt(@Valid @RequestBody SendAtRequest request) {
        notificationService.sendAt(request);
        return ResponseEntity.ok(StandardResponse.success(null));
    }

    @DeleteMapping("/{notificationId}/schedule")
    public ResponseEntity<StandardResponse<Void>> cancelScheduledNotification(@PathVariable UUID notificationId) {
        notificationService.cancelScheduledNotification(notificationId);
        return ResponseEntity.ok(StandardResponse.success(null));
    }
}

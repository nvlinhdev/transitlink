package vn.edu.fpt.transitlink.notification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.notification.dto.NotificationRegisterRequest;
import vn.edu.fpt.transitlink.notification.dto.NotificationUnregisterRequest;
import vn.edu.fpt.transitlink.notification.service.NoficationService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RestController("/api/notifications")
public class NotificationController {
    private final NoficationService notificationService;

    public NotificationController(NoficationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<StandardResponse<Boolean>> register(
            @RequestBody NotificationRegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/unregister")
    public ResponseEntity<StandardResponse<Boolean>> unregister(
            @RequestBody NotificationUnregisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}

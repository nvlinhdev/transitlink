package vn.edu.fpt.transitlink.notification.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationPriority;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateNotificationRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private Map<String, Object> data;
    private NotificationPriority priority;
    private String topic;
    @NotEmpty
    private List<UUID> accountIds;
}


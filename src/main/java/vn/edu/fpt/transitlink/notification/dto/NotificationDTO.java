package vn.edu.fpt.transitlink.notification.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private String title;
    private String content;
    private Map<String, Object> data;
    private String priority;
    private String topic;
    private String status;
    private OffsetDateTime sentAt;
    private Boolean read;
    private OffsetDateTime readAt;
}


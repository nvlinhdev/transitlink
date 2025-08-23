package vn.edu.fpt.transitlink.notification.dto;

import java.time.OffsetDateTime;

public record NotificationDTO(
    String id,
    String title,
    String content,
    boolean read,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

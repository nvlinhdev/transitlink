package vn.edu.fpt.transitlink.notification.dto;

public record NotificationDTO(
    String id,
    String title,
    String content,
    boolean read,
    String createdAt,
    String updatedAt
) {
}

package vn.edu.fpt.transitlink.notification.dto;

public record NotificationUnregisterRequest(
        String pushToken,
        String deviceType,
        String deviceId
) {
}

package vn.edu.fpt.transitlink.notification.dto;

public record NotificationRegisterRequest(
        String pushToken,
        String deviceType,
        String deviceId
) {
}

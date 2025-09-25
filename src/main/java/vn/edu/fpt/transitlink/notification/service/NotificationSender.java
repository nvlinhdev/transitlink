package vn.edu.fpt.transitlink.notification.service;

import java.util.UUID;

public interface NotificationSender {
    void send(UUID notificationId);
}


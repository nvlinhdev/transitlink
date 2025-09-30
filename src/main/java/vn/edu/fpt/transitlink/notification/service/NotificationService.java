package vn.edu.fpt.transitlink.notification.service;

import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.request.CreateNotificationRequest;
import vn.edu.fpt.transitlink.notification.request.SendAtRequest;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationDTO createNotification(CreateNotificationRequest request);

    NotificationDTO updateNotification(UUID notificationId, CreateNotificationRequest request);

    void deleteNotification(UUID accountId, UUID notificationId);

    void sendNotificationToMobile(UUID notificationId);
    void sendNotificationToWeb(UUID notificationId);
    void sendAt(SendAtRequest request);
    void cancelScheduledNotification(UUID notificationId);
    void sendToTopic(String topic, UUID notificationId);

    void markAsRead(UUID accountId, UUID notificationId);

    List<NotificationDTO> getNotificationsByAccount(UUID accountId, int page, int size);
    Long countNotificationsByAccount(UUID accountId);
    Long countNotificationNotRead(UUID accountId);
}

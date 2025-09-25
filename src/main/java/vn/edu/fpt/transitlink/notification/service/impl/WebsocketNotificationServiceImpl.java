package vn.edu.fpt.transitlink.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.entity.Notification;
import vn.edu.fpt.transitlink.notification.entity.NotificationAccount;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationStatus;
import vn.edu.fpt.transitlink.notification.mapper.NotificationMapper;
import vn.edu.fpt.transitlink.notification.repository.NotificationAccountRepository;
import vn.edu.fpt.transitlink.notification.repository.NotificationRepository;
import vn.edu.fpt.transitlink.notification.service.NotificationSender;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebsocketNotificationServiceImpl implements NotificationSender {

    private final NotificationRepository notificationRepository;
    private final NotificationAccountRepository notificationAccountRepository;
    private final NotificationMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void send(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        List<NotificationAccount> accounts = notificationAccountRepository.findAllByNotification_Id(notificationId);
        NotificationDTO dto = mapper.toDTO(notification);

        for (var na : accounts) {
            messagingTemplate.convertAndSendToUser(
                    na.getAccountId().toString(),
                    "/queue/notifications",
                    dto
            );
            na.setDeliveredAt(OffsetDateTime.now());
        }

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(OffsetDateTime.now());
        notificationRepository.save(notification);
    }
}

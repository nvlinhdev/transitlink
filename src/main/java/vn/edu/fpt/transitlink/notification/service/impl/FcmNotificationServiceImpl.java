package vn.edu.fpt.transitlink.notification.service.impl;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.notification.dto.NotificationTokenDTO;
import vn.edu.fpt.transitlink.notification.entity.Notification;
import vn.edu.fpt.transitlink.notification.entity.NotificationAccount;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationStatus;
import vn.edu.fpt.transitlink.notification.mapper.NotificationMapper;
import vn.edu.fpt.transitlink.notification.repository.NotificationAccountRepository;
import vn.edu.fpt.transitlink.notification.repository.NotificationRepository;
import vn.edu.fpt.transitlink.notification.service.NotificationSender;
import vn.edu.fpt.transitlink.notification.service.NotificationTokenService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmNotificationServiceImpl implements NotificationSender {

    private final NotificationRepository notificationRepository;
    private final NotificationAccountRepository notificationAccountRepository;
    private final NotificationMapper mapper;
    private final NotificationTokenService notificationTokenService;

    @Override
    public void send(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        List<NotificationAccount> accounts = notificationAccountRepository.findAllByNotification_Id(notificationId);

        List<String> tokenList = accounts.stream()
                .flatMap(na -> notificationTokenService.getActiveTokens(na.getAccountId()).stream())
                .map(NotificationTokenDTO::getToken)
                .distinct()
                .collect(Collectors.toList());

        if (tokenList.isEmpty()) {
            log.warn("No active tokens found for notification {}", notificationId);
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokenList)
                .putData("notificationId", notification.getId().toString())
                .putData("title", notification.getTitle())
                .putData("content", notification.getContent())
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getContent())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setChannelId("general_notifications")
                                .setSound("default")
                                .build())
                        .build())
                .build();

        boolean anySuccess = false;

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            anySuccess = response.getSuccessCount() > 0;

            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    notificationTokenService.deactivateToken(tokenList.get(i));
                }
            }

            if (anySuccess) {
                accounts.forEach(na -> na.setDeliveredAt(OffsetDateTime.now()));
            }
        } catch (FirebaseMessagingException e) {
            log.error("Error sending notification {}: {}", notificationId, e.getMessage());
        }

        notification.setStatus(anySuccess ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(OffsetDateTime.now());
        notificationRepository.save(notification);
    }
}

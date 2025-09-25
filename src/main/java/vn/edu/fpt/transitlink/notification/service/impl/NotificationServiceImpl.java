package vn.edu.fpt.transitlink.notification.service.impl;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.dto.NotificationTokenDTO;
import vn.edu.fpt.transitlink.notification.entity.Notification;
import vn.edu.fpt.transitlink.notification.entity.NotificationAccount;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationStatus;
import vn.edu.fpt.transitlink.notification.mapper.NotificationMapper;
import vn.edu.fpt.transitlink.notification.repository.NotificationAccountRepository;
import vn.edu.fpt.transitlink.notification.repository.NotificationRepository;
import vn.edu.fpt.transitlink.notification.request.CreateNotificationRequest;
import vn.edu.fpt.transitlink.notification.request.SendAtRequest;
import vn.edu.fpt.transitlink.notification.service.NotificationService;
import vn.edu.fpt.transitlink.notification.service.NotificationTokenService;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationAccountRepository notificationAccountRepository;
    private final NotificationMapper mapper;
    private final FcmNotificationServiceImpl fcmNotificationService;
    private final WebsocketNotificationServiceImpl websocketNotificationService;
    private final TaskScheduler taskScheduler;

    private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public NotificationDTO createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setData(request.getData());
        notification.setPriority(request.getPriority());
        notification.setTopic(request.getTopic());
        notification.setStatus(NotificationStatus.PENDING);

        Notification saved = notificationRepository.save(notification);

        if (request.getAccountIds() != null && !request.getAccountIds().isEmpty()) {
            List<NotificationAccount> accounts = request.getAccountIds().stream()
                    .map(accountId -> {
                        NotificationAccount na = new NotificationAccount();
                        na.setNotification(saved);
                        na.setAccountId(accountId);
                        return na;
                    }).toList();
            notificationAccountRepository.saveAll(accounts);
        }

        return mapper.toDTO(saved);
    }

    @Override
    public NotificationDTO updateNotification(UUID notificationId, CreateNotificationRequest request) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setData(request.getData());
        notification.setPriority(request.getPriority());
        notification.setTopic(request.getTopic());

        return mapper.toDTO(notificationRepository.save(notification));
    }

    @Override
    public void deleteNotification(UUID accountId, UUID notificationId) {
        var na = notificationAccountRepository.findByAccountIdAndNotification_Id(accountId, notificationId)
                .orElseThrow(() -> new IllegalArgumentException("NotificationAccount not found"));
        notificationAccountRepository.delete(na);
    }

    @Override
    public void sendNotificationToMobile(UUID notificationId) {
        fcmNotificationService.send(notificationId);
    }

    @Override
    public void sendNotificationToWeb(UUID notificationId) {
        websocketNotificationService.send(notificationId);
    }

    @Override
    public void sendAt(SendAtRequest request) {
        notificationRepository.findById(request.getNotificationId())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        Date scheduleDate = Date.from(request.getScheduleTime().toInstant());

        Runnable task = () -> sendNotificationToMobile(request.getNotificationId());

        ScheduledFuture<?> future = taskScheduler.schedule(task, scheduleDate);

        scheduledTasks.put(request.getNotificationId(), future);

        log.info("Notification {} scheduled at {}", request.getNotificationId(), request.getScheduleTime());
    }

    @Override
    public void cancelScheduledNotification(UUID notificationId) {
        ScheduledFuture<?> future = scheduledTasks.remove(notificationId);
        if (future != null) {
            future.cancel(false);
            log.info("Cancelled scheduled notification {}", notificationId);
        }
    }

    @Override
    public void sendToTopic(String topic, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        Message message = Message.builder()
                .setTopic(topic)
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

        try {
            FirebaseMessaging.getInstance().send(message);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(OffsetDateTime.now());
            notificationRepository.save(notification);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending notification {} to topic {}: {}", notificationId, topic, e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void markAsRead(UUID accountId, UUID notificationId) {
        var na = notificationAccountRepository.findByAccountIdAndNotification_Id(accountId, notificationId)
                .orElseThrow(() -> new IllegalArgumentException("NotificationAccount not found"));
        na.setRead(true);
        na.setReadAt(OffsetDateTime.now());
        notificationAccountRepository.save(na);
    }

    @Override
    public List<NotificationDTO> getNotificationsByAccount(UUID accountId, int page, int size) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, size,
                Sort.by("notification.sentAt").descending());

        var notificationAccounts = notificationAccountRepository.findAllByAccountId(accountId, pageable);

        return notificationAccounts.stream()
                .map(na -> mapper.toDTO(na.getNotification()))
                .toList();
    }

    @Override
    public Long countNotificationsByAccount(UUID accountId) {
        return notificationAccountRepository.countByAccountId(accountId);
    }

    @Override
    public Long countNotificationNotRead(UUID accountId) {
        return notificationAccountRepository.countByAccountIdAndReadFalse(accountId);
    }
}

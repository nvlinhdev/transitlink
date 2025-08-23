package vn.edu.fpt.transitlink.notification.job;

import org.springframework.stereotype.Component;
import vn.edu.fpt.transitlink.notification.entity.Notification;
import vn.edu.fpt.transitlink.notification.repository.NotificationRepository;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeleteCleanupJob;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.Duration;
import java.util.UUID;

@Component
public class NotificationSoftDeleteCleanupJob extends BaseSoftDeleteCleanupJob<Notification, UUID> {
    private final NotificationRepository notificationRepository;

    public NotificationSoftDeleteCleanupJob(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    @Override
    protected SoftDeletableRepository<Notification, UUID> getRepository() {
        return notificationRepository;
    }

    @Override
    protected Duration getRetentionDuration() {
        return Duration.ofDays(30);
    }
}

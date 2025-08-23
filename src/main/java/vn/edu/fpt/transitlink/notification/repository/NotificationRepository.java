package vn.edu.fpt.transitlink.notification.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.notification.entity.Notification;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface NotificationRepository extends SoftDeletableRepository<Notification, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.deleted = true AND n.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

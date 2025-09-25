package vn.edu.fpt.transitlink.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.notification.entity.Notification;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}

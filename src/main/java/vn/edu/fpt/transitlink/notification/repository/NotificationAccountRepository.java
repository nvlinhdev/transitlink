package vn.edu.fpt.transitlink.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.notification.entity.NotificationAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationAccountRepository extends JpaRepository<NotificationAccount, UUID> {

    // lấy tất cả mapping của 1 account
    Page<NotificationAccount> findAllByAccountId(UUID accountId, Pageable pageable);

    Optional<NotificationAccount> findByAccountIdAndNotification_Id(UUID accountId, UUID notificationId);

    List<NotificationAccount> findAllByNotification_IdAndAccountIdIn(UUID notificationId, List<UUID> accountIds);
    // đếm số thông báo chưa đọc
    Long countByAccountIdAndReadFalse(UUID accountId);

    Long countByAccountId(UUID accountId);

    List<NotificationAccount> findAllByNotification_Id(UUID notificationId);
}

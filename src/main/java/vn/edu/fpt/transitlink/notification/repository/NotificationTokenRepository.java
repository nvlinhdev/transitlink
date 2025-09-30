package vn.edu.fpt.transitlink.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.notification.entity.NotificationToken;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationTokenStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, UUID> {

    // tìm tất cả token ACTIVE của 1 account
    List<NotificationToken> findAllByAccountIdAndStatus(UUID accountId, NotificationTokenStatus status);

    // tìm token theo giá trị token
    Optional<NotificationToken> findByToken(String token);
}

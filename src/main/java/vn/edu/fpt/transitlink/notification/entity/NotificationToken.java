package vn.edu.fpt.transitlink.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.transitlink.notification.enumeration.DevicePlatform;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationTokenStatus;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "notification_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_notification_token", columnNames = {"token"})
        },
        indexes = {
                @Index(name = "idx_notification_token_account", columnList = "account_id")
        }
)
public class NotificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DevicePlatform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationTokenStatus status = NotificationTokenStatus.ACTIVE;
}


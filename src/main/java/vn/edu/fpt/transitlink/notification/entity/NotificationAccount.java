package vn.edu.fpt.transitlink.notification.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notification_account_mapping",
        indexes = {
                @Index(name = "idx_notification_account_account_id", columnList = "account_id"),
                @Index(name = "idx_notification_account_notification_id", columnList = "notification_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private Boolean read = false;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Column(name = "read_at")
    private OffsetDateTime readAt;
}


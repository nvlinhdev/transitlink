package vn.edu.fpt.transitlink.notification.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationPriority;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_topic", columnList = "topic"),
                @Index(name = "idx_notifications_status", columnList = "status")
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority;

    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    private OffsetDateTime sentAt;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NotificationAccount> userNotifications = new ArrayList<>();
}


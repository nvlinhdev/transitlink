package vn.edu.fpt.transitlink.notification.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendToTopicRequest {
    private UUID notificationId;
    private String topic;
}

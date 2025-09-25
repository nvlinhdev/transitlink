package vn.edu.fpt.transitlink.notification.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendToAccountsRequest {
    private UUID notificationId;
    private List<UUID> accountIds;
}

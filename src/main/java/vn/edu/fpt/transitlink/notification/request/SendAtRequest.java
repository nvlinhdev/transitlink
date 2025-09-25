package vn.edu.fpt.transitlink.notification.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SendAtRequest {

    @NotNull
    private UUID notificationId;

    @NotNull
    @Future
    private OffsetDateTime scheduleTime;
}

package vn.edu.fpt.transitlink.notification.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NotificationTokenDTO {
    private UUID id;
    private UUID accountId;
    private String token;
    private String platform;
    private String status;
}


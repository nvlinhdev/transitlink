package vn.edu.fpt.transitlink.notification.service;

import vn.edu.fpt.transitlink.notification.dto.NotificationTokenDTO;
import vn.edu.fpt.transitlink.notification.request.RegisterTokenRequest;

import java.util.List;
import java.util.UUID;

public interface NotificationTokenService {

    NotificationTokenDTO registerToken(UUID accountId, RegisterTokenRequest request);

    void deactivateToken(String token);

    List<NotificationTokenDTO> getActiveTokens(UUID accountId);
}

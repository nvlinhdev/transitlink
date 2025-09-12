package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.enumeration.NotificationType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AccountNotificationService {
    CompletableFuture<Boolean> sendNotificationEmail(String email, NotificationType type, Map<String, Object> templateVariables);
}

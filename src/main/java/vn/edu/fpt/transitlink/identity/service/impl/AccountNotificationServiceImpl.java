package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.enumeration.NotificationType;
import vn.edu.fpt.transitlink.identity.service.AccountNotificationService;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailRequest;
import vn.edu.fpt.transitlink.mail_sender.service.AsyncEmailService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNotificationServiceImpl implements AccountNotificationService {

    private final AsyncEmailService asyncEmailService;

    @Override
    public CompletableFuture<Boolean> sendNotificationEmail(String email, NotificationType type, Map<String, Object> templateVariables) {
        try {
            EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .subject(type.getEmailSubject())
                .templateName(type.getEmailTemplate())
                .variables(templateVariables)
                .build();

            return asyncEmailService.sendEmailAsync(emailRequest);
        } catch (Exception e) {
            log.error("Failed to send notification email to {}: {}", email, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }
}

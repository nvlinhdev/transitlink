package vn.edu.fpt.transitlink.mail_sender.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.mail_sender.dto.BulkEmailRequest;
import vn.edu.fpt.transitlink.mail_sender.dto.BulkEmailResult;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailRequest;
import vn.edu.fpt.transitlink.mail_sender.service.AsyncEmailService;
import vn.edu.fpt.transitlink.mail_sender.service.EmailService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AsyncEmailServiceImpl implements AsyncEmailService {

    private final EmailService emailService;

    public AsyncEmailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Boolean> sendEmailAsync(EmailRequest emailRequest) {
        try {
            emailService.sendHtmlEmail(emailRequest);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Error sending async email to: {}", emailRequest.to(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<BulkEmailResult> sendBulkEmailsAsync(BulkEmailRequest bulkRequest) {
        return CompletableFuture.supplyAsync(() -> {
            List<EmailRequest> emails = bulkRequest.emails();
            int batchSize = bulkRequest.batchSize();
            long delay = bulkRequest.delayBetweenBatches();

            int successCount = 0;
            int failureCount = 0;
            for (int i = 0; i < emails.size(); i += batchSize) {
                List<EmailRequest> batch = emails.subList(i,
                        Math.min(i + batchSize, emails.size()));

                try {
                    emailService.sendBulkEmails(batch);
                    successCount += batch.size();

                    if (i + batchSize < emails.size() && delay > 0) {
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Batch email sending interrupted at index {}", i, e);
                    failureCount += batch.size();
                } catch (Exception e) {
                    log.error("Unexpected error sending batch starting at index {}", i, e);
                    failureCount += batch.size();
                }

            }


            log.info("Bulk email completed. Success: {}, Failed: {}",
                    successCount, failureCount);

            return BulkEmailResult.builder()
                    .totalEmails(emails.size())
                    .successCount(successCount)
                    .failureCount(failureCount)
                    .completed(true)
                    .build();
        });
    }
}

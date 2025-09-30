package vn.edu.fpt.transitlink.mail_sender.service;

import vn.edu.fpt.transitlink.mail_sender.dto.BulkEmailRequest;
import vn.edu.fpt.transitlink.mail_sender.dto.BulkEmailResult;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailRequest;

import java.util.concurrent.CompletableFuture;

public interface AsyncEmailService {
    CompletableFuture<Boolean> sendEmailAsync(EmailRequest emailRequest);
    CompletableFuture<BulkEmailResult> sendBulkEmailsAsync(BulkEmailRequest bulkRequest);
}

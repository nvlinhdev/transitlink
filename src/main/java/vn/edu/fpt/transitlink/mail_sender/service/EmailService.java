package vn.edu.fpt.transitlink.mail_sender.service;

import jakarta.mail.MessagingException;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailRequest;

import java.util.List;

public interface EmailService {
    void sendHtmlEmail(EmailRequest emailRequest) throws MessagingException;
    void sendBulkEmails(List<EmailRequest> emailRequests);
}

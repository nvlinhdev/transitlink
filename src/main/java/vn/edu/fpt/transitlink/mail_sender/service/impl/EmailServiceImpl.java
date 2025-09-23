package vn.edu.fpt.transitlink.mail_sender.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.edu.fpt.transitlink.mail_sender.config.MailProperties;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailAttachment;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailRequest;
import vn.edu.fpt.transitlink.mail_sender.service.EmailService;
import vn.edu.fpt.transitlink.shared.exception.SystemException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailProperties mailProperties;

    public EmailServiceImpl(JavaMailSender mailSender,
                        SpringTemplateEngine templateEngine,
                        MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendHtmlEmail(EmailRequest emailRequest) throws MessagingException {
        if (!mailProperties.enabled()) {
            log.info("Email service disabled, skipping email to: {}", emailRequest.to());
            return;
        }

        MimeMessage mimeMessage = createMimeMessage(emailRequest);
        mailSender.send(mimeMessage);
        log.info("Email sent successfully to: {}", emailRequest.to());
    }

    @Override
    public void sendBulkEmails(List<EmailRequest> emailRequests) {
        if (emailRequests == null || emailRequests.isEmpty()) {
            log.warn("No emails to send");
            return;
        }

        // Create a collector for valid messages
        List<MimeMessage> validMessages = new ArrayList<>();
        int failedCount = 0;
        int totalCount = emailRequests.size();

        // Process each request separately
        for (EmailRequest request : emailRequests) {
            try {
                validMessages.add(createMimeMessage(request));
            } catch (Exception e) {
                failedCount++;
                log.error("Failed to create email for recipient: {}", request.to(), e);
            }
        }

        // Send all valid messages in a single batch
        if (!validMessages.isEmpty()) {
            try {
                mailSender.send(validMessages.toArray(new MimeMessage[0]));
                log.info("Sent {} emails successfully (failed: {}, total: {})",
                        validMessages.size(), failedCount, totalCount);
            } catch (Exception e) {
                log.error("Failed to send batch email", e);
                throw new SystemException(e.getMessage(), e);
            }
        } else {
            log.warn("No valid emails to send after processing {} requests", totalCount);
        }
    }

    private MimeMessage createMimeMessage(EmailRequest emailRequest) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(emailRequest.to());
        helper.setSubject(emailRequest.subject());
        helper.setFrom(mailProperties.from());

        if (emailRequest.cc() != null && emailRequest.cc().length > 0) {
            helper.setCc(emailRequest.cc());
        }

        if (emailRequest.bcc() != null && emailRequest.bcc().length > 0) {
            helper.setBcc(emailRequest.bcc());
        }

        String htmlContent = processContent(emailRequest);
        helper.setText(htmlContent, true);

        if (emailRequest.attachments() != null && !emailRequest.attachments().isEmpty()) {
            for (EmailAttachment attachment : emailRequest.attachments()) {
                helper.addAttachment(attachment.filename(),
                        new ByteArrayDataSource(attachment.content(), attachment.contentType()));
            }
        }

        return mimeMessage;
    }

    private String processContent(EmailRequest emailRequest) {
        if (StringUtils.hasText(emailRequest.templateName())) {
            Context context = new Context();
            if (emailRequest.variables() != null) {
                context.setVariables(emailRequest.variables());
            }
            return templateEngine.process(emailRequest.templateName(), context);
        }
        return emailRequest.content();
    }
}


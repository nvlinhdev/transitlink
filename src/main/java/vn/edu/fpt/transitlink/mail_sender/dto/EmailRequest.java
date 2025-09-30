package vn.edu.fpt.transitlink.mail_sender.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record EmailRequest(
    String to,
    String[] cc,
    String[] bcc,
    String subject,
    String content,
    String templateName,
    Map<String, Object> variables,
    List<EmailAttachment> attachments
) {}

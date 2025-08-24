package vn.edu.fpt.transitlink.mail_sender.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BulkEmailRequest(
    List<EmailRequest> emails,
    int batchSize,
    long delayBetweenBatches // milliseconds
) {}

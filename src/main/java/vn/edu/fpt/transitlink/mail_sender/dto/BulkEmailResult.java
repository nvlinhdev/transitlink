package vn.edu.fpt.transitlink.mail_sender.dto;

import lombok.Builder;

@Builder
public record BulkEmailResult (
    int totalEmails,
    int successCount,
    int failureCount,
    boolean completed
) {}
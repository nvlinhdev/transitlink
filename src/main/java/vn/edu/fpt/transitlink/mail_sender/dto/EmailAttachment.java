package vn.edu.fpt.transitlink.mail_sender.dto;

import lombok.Builder;

@Builder
public record EmailAttachment (
    String filename,
    byte[] content,
    String contentType
) {
}

package vn.edu.fpt.transitlink.mail_sender.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        String host,
        Integer port,
        String username,
        String password,
        String from,
        boolean enabled
) {}


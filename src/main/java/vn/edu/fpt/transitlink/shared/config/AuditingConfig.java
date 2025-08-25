package vn.edu.fpt.transitlink.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing(
        auditorAwareRef = "auditorProvider",
        dateTimeProviderRef = "auditingDateTimeProvider")
@Profile("!test")
public class AuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new JwtAuditorAware();
    }
}

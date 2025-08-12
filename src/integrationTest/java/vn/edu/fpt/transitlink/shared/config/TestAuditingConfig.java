package vn.edu.fpt.transitlink.shared.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@TestConfiguration
@EnableJpaAuditing(
        auditorAwareRef = "testAuditorProvider",
        dateTimeProviderRef = "testDateTimeProvider"
)
@Profile("test")
public class TestAuditingConfig {

    @Bean
    @Primary
    public AuditorAware<UUID> testAuditorProvider() {
        return () -> Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Bean
    @Primary
    public DateTimeProvider testDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}
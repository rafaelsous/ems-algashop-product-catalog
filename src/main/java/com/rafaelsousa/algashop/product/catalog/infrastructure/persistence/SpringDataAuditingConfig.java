package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence;

import com.rafaelsousa.algashop.product.catalog.application.security.SecurityChecks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableMongoAuditing(
    dateTimeProviderRef = "auditingDateTimeProvider",
    auditorAwareRef = "auditorProvider"
)
public class SpringDataAuditingConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));
    }

    @Bean
    public AuditorAware<UUID> auditorProvider(SecurityChecks securityChecks) {
        return () -> {
            if (!securityChecks.isAuthenticated() || securityChecks.isMachineAuthenticated()) {
                return Optional.empty();
            }

            return Optional.of(securityChecks.getAuthenticatedUserId());
        };
    }
}
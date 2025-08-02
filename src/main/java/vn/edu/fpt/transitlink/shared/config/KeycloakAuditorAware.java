package vn.edu.fpt.transitlink.shared.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

public class KeycloakAuditorAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            try {
                return Optional.of(UUID.fromString(sub));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}


package vn.edu.fpt.transitlink.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Storage storage,
        Security security
) {

    public record Storage(
            String provider,
            Path rootPath,
            String baseUrl
    ) {}

    public record Security(
            List<String> permitAll,
            Cors cors,
            Jwt jwt,
            Oauth2 oauth2
    ) {
        public record Cors(
                List<String> allowedOrigins,
                List<String> allowedMethods,
                List<String> allowedHeaders,
                boolean allowCredentials
        ) {}

        public record Jwt(
                String secret,
                AccessToken accessToken,
                RefreshToken refreshToken
        ) {
            public record AccessToken(long expiration) {}
            public record RefreshToken(long expiration) {}
        }

        public record Oauth2(
                List<String> redirectUris
        ) {}
    }
}

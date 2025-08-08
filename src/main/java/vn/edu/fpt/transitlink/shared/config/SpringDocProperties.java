package vn.edu.fpt.transitlink.shared.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "springdoc")
public record SpringDocProperties(
        Info info,
        List<Server> servers,
        Oauth oauth,
        List<Group> groups
) {
    public record Oauth(
            String authorizationUrl,
            String tokenUrl,
            String refreshUrl,
            Map<String, String> scopes
    ) {}

    public record Group(
            String group,
            String paths
    ) {}
}

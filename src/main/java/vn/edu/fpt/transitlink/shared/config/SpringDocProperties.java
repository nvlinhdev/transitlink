package vn.edu.fpt.transitlink.shared.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "springdoc")
public class SpringDocProperties {

    private Info info;
    private List<Server> servers;
    private Oauth oauth;
    private List<Group> groups;

    @Data
    public static class Oauth {
        private String authorizationUrl;
        private String tokenUrl;
        private String refreshUrl;
        private Map<String, String> scopes;
    }

    @Data
    public static class Group {
        private String group;
        private String paths;
    }
}

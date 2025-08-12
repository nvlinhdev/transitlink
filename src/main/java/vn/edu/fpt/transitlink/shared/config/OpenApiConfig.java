package vn.edu.fpt.transitlink.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile({"!test", "!prod"})
public class OpenApiConfig {

    private final SpringDocProperties props;

    @Bean
    public OpenAPI customOpenAPI() {
        Scopes scopes = new Scopes();
        props.oauth().scopes().forEach(scopes::addString);

        return new OpenAPI()
                .info(props.info())
                .servers(props.servers())
                .components(new Components().addSecuritySchemes("keycloak",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("OAuth2 Authorization Code Flow with PKCE")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(props.oauth().authorizationUrl())
                                                .tokenUrl(props.oauth().tokenUrl())
                                                .refreshUrl(props.oauth().refreshUrl())
                                                .scopes(scopes)
                                        ))))
                .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }

    @Bean
    public List<GroupedOpenApi> groupedApis() {
        List<GroupedOpenApi> apis = new ArrayList<>();
        for (SpringDocProperties.Group group : props.groups()) {
            apis.add(GroupedOpenApi.builder()
                    .group(group.group())
                    .pathsToMatch(group.paths())
                    .build());
        }
        return apis;
    }
}

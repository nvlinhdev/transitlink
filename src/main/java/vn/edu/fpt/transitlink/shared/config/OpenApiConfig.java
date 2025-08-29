package vn.edu.fpt.transitlink.shared.config;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import vn.edu.fpt.transitlink.shared.annotation.SkipGlobalErrorResponses;
import vn.edu.fpt.transitlink.shared.dto.*;

import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"!(test|prod)"})
public class OpenApiConfig {

    private final SpringDocProperties props;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(props.info())
                .servers(props.servers())
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme")
                        ))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    @Bean
    public OperationCustomizer skipMarkerCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getBeanType().isAnnotationPresent(SkipGlobalErrorResponses.class)
                    || handlerMethod.hasMethodAnnotation(SkipGlobalErrorResponses.class)) {
                operation.addExtension("x-skip-global-errors", Boolean.TRUE);
            }
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer errorResponsesCustomizer() {

        final Map<String, String> GLOBAL_ERROR_MAP = new LinkedHashMap<>() {{
            put("400", "Bad Request - Validation or malformed input");
            put("401", "Unauthorized - Authentication required or invalid token");
            put("403", "Forbidden - Insufficient permissions");
            put("404", "Not Found - Resource not found");
            put("409", "Conflict - Data integrity or state conflict");
            put("422", "Unprocessable Entity - Business rule (if used)");
            put("500", "Internal Server Error - Unexpected error");
        }};

        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            Components components = openApi.getComponents();
            if (components.getSchemas() == null) {
                components.setSchemas(new LinkedHashMap<>());
            }

            // Force resolve & register schemas (SimpleErrorResponse, ValidationErrorResponse, ValidationError)
            registerSchemaIfAbsent(components, SimpleErrorResponse.class);
            registerSchemaIfAbsent(components, ValidationErrorResponse.class);
            registerSchemaIfAbsent(components, ValidationError.class);

            if (openApi.getPaths() == null) return;

            Schema<?> simpleRef = new Schema<>().$ref("#/components/schemas/SimpleErrorResponse");
            Schema<?> validationRef = new Schema<>().$ref("#/components/schemas/ValidationErrorResponse");

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {

                        if (Boolean.TRUE.equals(operation.getExtensions() != null
                                ? operation.getExtensions().get("x-skip-global-errors")
                                : null)) {
                            return;
                        }

                        ApiResponses responses = operation.getResponses();
                        if (responses == null) {
                            responses = new ApiResponses();
                            operation.setResponses(responses);
                        }

                        for (Map.Entry<String, String> entry : GLOBAL_ERROR_MAP.entrySet()) {
                            String code = entry.getKey();
                            String desc = entry.getValue();
                            if (responses.containsKey(code)) {
                                continue;
                            }

                            ApiResponse apiResponse;
                            Schema<?> schemaToUse;

                            if (code.equals("400")) {
                                // oneOf for 400
                                schemaToUse = new ComposedSchema()
                                        .oneOf(List.of(simpleRef, validationRef))
                                        .description("Either a simple error or a validation error object");
                            } else if (code.equals("422")) {
                                // If you end up using 422 for validation only, switch to validationRef here
                                schemaToUse = simpleRef; // change to validationRef if adopting 422 for validation
                            } else {
                                schemaToUse = simpleRef;
                            }

                            apiResponse = new ApiResponse()
                                    .description(desc)
                                    .content(new Content().addMediaType("application/json",
                                            new MediaType().schema(schemaToUse)));

                            responses.addApiResponse(code, apiResponse);
                        }
                    })
            );
        };
    }

    private void registerSchemaIfAbsent(Components components, Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (components.getSchemas().containsKey(name)) return;
        Map<String, Schema> resolved = ModelConverters.getInstance().read(clazz);
        resolved.forEach((n, s) -> {
            if (!components.getSchemas().containsKey(n)) {
                components.addSchemas(n, s);
            }
        });
    }

    @Bean
    public List<GroupedOpenApi> groupedApis(OpenApiCustomizer errorResponsesCustomizer,
                                            OperationCustomizer skipMarkerCustomizer) {
        List<GroupedOpenApi> apis = new ArrayList<>();
        for (SpringDocProperties.Group group : props.groups()) {
            apis.add(GroupedOpenApi.builder()
                    .group(group.group())
                    .pathsToMatch(group.paths())
                    .addOpenApiCustomizer(errorResponsesCustomizer)
                    .addOperationCustomizer(skipMarkerCustomizer)
                    .build());
        }
        return apis;
    }

    @Bean
    public SwaggerIndexPageTransformer swaggerIndexPageTransformer(
            SwaggerUiConfigProperties a,
            SwaggerUiOAuthProperties b,
            SwaggerWelcomeCommon c,
            ObjectMapperProvider d) {
        return new SwaggerCodeBlockTransformer(a, b, c, d);
    }

    @Bean
    public ModelConverter standardResponseModelConverter() {
        return new StandardResponseModelConverter();
    }
}
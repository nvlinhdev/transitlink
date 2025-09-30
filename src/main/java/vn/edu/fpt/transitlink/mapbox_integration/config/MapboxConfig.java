package vn.edu.fpt.transitlink.mapbox_integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.fpt.transitlink.mapbox_integration.client.direction.DirectionClient;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.OptimizationClient;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.SearchClient;

import java.net.URI;
import java.util.Map;

@Configuration
public class MapboxConfig {

    @Bean
    RestClient mapboxRestClient(RestClient.Builder builder,
                                @Value("${mapbox.access-token}") String accessToken) {
        return builder
                .baseUrl("https://api.mapbox.com")
                .requestInterceptor((request, body, execution) -> {
                    // Build lại URI, thêm access_token vào query
                    var uri = UriComponentsBuilder.fromUri(request.getURI())
                            .queryParam("access_token", accessToken)
                            .build(true)   // true để giữ nguyên encoding
                            .toUri();
                    var newRequest = new HttpRequest() {
                        @Override
                        public HttpMethod getMethod() {
                            return request.getMethod();
                        }

                        @Override
                        public Map<String, Object> getAttributes() {
                            return request.getAttributes();
                        }

                        @Override
                        public URI getURI() {
                            return uri;
                        }

                        @Override
                        public HttpHeaders getHeaders() {
                            return request.getHeaders();
                        }
                    };
                    return execution.execute(newRequest, body);
                })
                .build();
    }



    @Bean
    OptimizationClient routeClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(OptimizationClient.class);
    }

    @Bean
    DirectionClient directionClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(DirectionClient.class);
    }

    @Bean
    SearchClient searchClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(SearchClient.class);
    }

}

package vn.edu.fpt.transitlink.mapbox_integration.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.spi.PlaceSearchProvider;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.SearchClient;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.SearchMapper;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response.FeatureCollection;
import vn.edu.fpt.transitlink.shared.exception.SystemException;
import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MapboxPlaceSearchProvider implements PlaceSearchProvider {
    private final SearchClient searchClient;

    @Override
    public List<PlaceDTO> searchPlaces(String query) {
        ResponseEntity<FeatureCollection> response = null;
        try {
            response = searchClient.search(URLEncoder.encode(query, StandardCharsets.UTF_8),
                    "vn", 5);
            FeatureCollection featureCollection = response.getBody();
            if (featureCollection == null || featureCollection.features() == null) {
                return List.of();
            }
            return SearchMapper.fromFeatureCollection(featureCollection);
        } catch (RestClientResponseException e){
            throw new ThirdPartyException(
                    "Search location fail", "Search Service", e.getStatusCode().value(), e.getResponseBodyAsString()
            );
        } catch (RuntimeException e) {
            throw new SystemException("Failed to search places", e);
        }
    }
}

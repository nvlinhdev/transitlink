package vn.edu.fpt.transitlink.mapbox_integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.spi.PlaceSearchProvider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class MapboxPlaceSearchProvider implements PlaceSearchProvider {
    @Value("${mapbox.access-token}")
    private String mapboxAccessToken;

    @Override
    public List<PlaceDTO> searchPlaces(String query) {
        String url = "https://api.mapbox.com/search/searchbox/v1/forward?q=" +
                URLEncoder.encode(query, StandardCharsets.UTF_8) +
                "&country=vn&limit=5&access_token=" + mapboxAccessToken;
        // Call the Mapbox API using RestTemplate or WebClient
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        // Parse the response and convert it to a list of PlaceDTO
        try {
            return MapboxMapper.mapboxToPlaceDTOs(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

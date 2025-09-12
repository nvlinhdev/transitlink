package vn.edu.fpt.transitlink.mapbox_integration.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/optimization")
public class OptimizationController {

    @Value("${mapbox.access-token}")
    private String mapboxAccessToken;

    @GetMapping("/route")
    public ResponseEntity<String> optimizeRoute(
            @RequestParam String profile,
            @RequestParam List<String> coordinates // Mỗi phần tử: "lon,lat"
    ) {
        String joinedCoordinates = String.join(";", coordinates);
        String url = "https://api.mapbox.com/optimized-trips/v1/mapbox/" + profile + "/" +
                joinedCoordinates + "?access_token=" + mapboxAccessToken +
                "&overview=full&geometries=geojson";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }
}

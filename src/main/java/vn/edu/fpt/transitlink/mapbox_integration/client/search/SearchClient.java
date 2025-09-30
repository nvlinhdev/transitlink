package vn.edu.fpt.transitlink.mapbox_integration.client.search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response.FeatureCollection;

@HttpExchange(url = "/search/searchbox/v1", accept = "application/json")
public interface SearchClient {

    @GetExchange("/forward")
    ResponseEntity<FeatureCollection> search(
            @RequestParam("q") String query,
            @RequestParam("country") String country,
            @RequestParam("limit") int limit
    );
}

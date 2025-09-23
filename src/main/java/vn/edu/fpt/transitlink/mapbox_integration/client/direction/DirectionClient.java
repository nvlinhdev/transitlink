package vn.edu.fpt.transitlink.mapbox_integration.client.direction;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import vn.edu.fpt.transitlink.mapbox_integration.client.direction.dto.response.DirectionResponse;

@HttpExchange(value = "/directions/v5", accept = "application/json")
public interface DirectionClient {

    @PostExchange(
            url = "/mapbox/driving",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<DirectionResponse> getDirections(@RequestBody MultiValueMap<String, String> body);
}

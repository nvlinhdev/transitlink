package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Vehicle(
        String name,
        @JsonProperty("routing_profile")
        String routingProfile,
        @JsonProperty("start_location")
        String startLocation,
        @JsonProperty("end_location")
        String endLocation,
        Map<String, Integer> capacities
) {
}

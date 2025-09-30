package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Shipment(
        String name,
        String from,
        String to,
        Map<String, Integer> size,
        @JsonProperty("pickup_duration")
        Integer pickupDuration,
        @JsonProperty("dropoff_duration")
        Integer dropoffDuration,
        @JsonProperty("pickup_times")
        List<TimeWindow> pickupTimes,
        @JsonProperty("dropoff_times")
        List<TimeWindow> dropoffTimes
) {
}

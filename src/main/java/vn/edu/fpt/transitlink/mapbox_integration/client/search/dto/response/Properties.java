package vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Properties(
        String name,
        @JsonProperty("mapbox_id") String mapboxId,
        Context context
) {}

package vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Neighborhood(
        String name
) {}

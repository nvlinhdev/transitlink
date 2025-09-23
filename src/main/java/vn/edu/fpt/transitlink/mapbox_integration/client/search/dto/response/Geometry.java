package vn.edu.fpt.transitlink.mapbox_integration.client.search.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Geometry(
        String type,
        List<Double> coordinates
) {}

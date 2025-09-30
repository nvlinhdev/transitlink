package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LocationMetadata(
        @JsonProperty("supplied_coordinate")
        List<Double> suppliedCoordinate,
        @JsonProperty("snapped_coordinate")
        List<Double> snappedCoordinate
) {}

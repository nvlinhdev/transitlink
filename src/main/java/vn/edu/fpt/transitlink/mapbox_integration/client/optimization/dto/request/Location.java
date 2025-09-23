package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Location(
        String name,
        List<Double> coordinates
) {}

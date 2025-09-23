package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record Stop(
        String type,
        String location,
        @JsonProperty("location_metadata")
        LocationMetadata locationMetadata,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant eta,
        Double odometer,
        Integer duration,
        List<String> pickups,
        List<String> dropoffs
) {}

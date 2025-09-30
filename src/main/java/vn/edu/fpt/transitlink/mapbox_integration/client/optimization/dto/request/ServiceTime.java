package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ServiceTime(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant earliest,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant latest,
        String type  // "strict", "soft", "soft_start", "soft_end"
) {}

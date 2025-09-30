package vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptimizationRequestBody(
        int version,
        List<Location> locations,
        List<Vehicle> vehicles,
        List<Shipment> shipments,
        Options options
) {
}
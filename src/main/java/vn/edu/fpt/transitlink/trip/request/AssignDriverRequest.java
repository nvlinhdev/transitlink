package vn.edu.fpt.transitlink.trip.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignDriverRequest(
        @NotNull(message = "Driver ID must not be null")
        UUID driverId,
        @NotNull(message = "Route ID must not be null")
        UUID routeId
) {}


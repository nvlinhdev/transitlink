package vn.edu.fpt.transitlink.location.dto;

import java.util.UUID;

public record DriverLocationMessage(
        UUID driverId,
        Double latitude,
        Double longitude
) {}

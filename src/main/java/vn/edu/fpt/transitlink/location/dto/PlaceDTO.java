package vn.edu.fpt.transitlink.location.dto;

import java.util.UUID;

public record PlaceDTO(
        UUID id,
        String name,
        Double latitude,
        Double longitude,
        String address
) {}

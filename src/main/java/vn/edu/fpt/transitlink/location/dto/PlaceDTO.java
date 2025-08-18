package vn.edu.fpt.transitlink.location.dto;

import java.awt.*;
import java.util.UUID;

public record PlaceDTO(
        UUID placeId,
        Point location,
        String address
) {
}

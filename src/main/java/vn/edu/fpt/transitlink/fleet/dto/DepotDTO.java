package vn.edu.fpt.transitlink.fleet.dto;

import java.util.UUID;

public record DepotDTO(
         UUID id,
         String name,
         UUID placeId
) {
}

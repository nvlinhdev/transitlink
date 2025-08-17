package vn.edu.fpt.transitlink.vehicle.dto;

import java.util.UUID;

public record VehicleDTO(
        UUID id,
        String plateNumber,
        String type,
        int capacity,
        String status

) {

}

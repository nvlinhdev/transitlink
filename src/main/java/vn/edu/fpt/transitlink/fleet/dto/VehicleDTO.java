package vn.edu.fpt.transitlink.fleet.dto;

import java.util.UUID;

public record VehicleDTO(
         UUID id,
         String license_plate,
         String vehicle_type,
         int capacity,
         String fuel_type,
         double fuel_consumption_rate,
         String status

) {

}

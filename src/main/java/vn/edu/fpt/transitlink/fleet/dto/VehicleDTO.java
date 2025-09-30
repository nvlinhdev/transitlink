package vn.edu.fpt.transitlink.fleet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.fleet.enumeration.FuelType;

import java.util.UUID;

@Schema(description = "Data Transfer Object representing a vehicle with its associated depot")
public record VehicleDTO(
        @Schema(description = "Unique identifier of the vehicle", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Name of the vehicle", example = "Bus #42")
        String name,

        @Schema(description = "License plate of the vehicle", example = "29A-12345")
        String licensePlate,

        @Schema(description = "Type of fuel used by the vehicle", example = "DIESEL")
        FuelType vehicleType,

        @Schema(description = "Maximum passenger capacity of the vehicle", example = "40")
        Integer capacity,

        @Schema(description = "Fuel consumption rate in liters per 100 kilometers", example = "25.5")
        Float fuelConsumptionRate,

        @Schema(description = "Depot where the vehicle is stationed")
        DepotDTO depot
) {
}

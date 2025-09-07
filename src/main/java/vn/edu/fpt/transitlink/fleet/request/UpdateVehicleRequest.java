package vn.edu.fpt.transitlink.fleet.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import vn.edu.fpt.transitlink.fleet.enumeration.FuelType;

import java.util.UUID;

@Schema(description = "Request object for updating an existing vehicle")
public record UpdateVehicleRequest(
        @Schema(description = "Name of the vehicle", example = "Bus #1")
        String name,

        @Schema(description = "License plate of the vehicle", example = "29A-12345")
        String licensePlate,

        @Schema(description = "Capacity of the vehicle (number of passengers)", example = "40")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @Schema(description = "Fuel type of the vehicle", example = "DIESEL")
        FuelType fuelType,

        @Schema(description = "Fuel consumption rate (liters per 100 km)", example = "25.5")
        Float fuelConsumptionRate,

        @Schema(description = "ID of the depot where the vehicle is stationed", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID depotId
) {}

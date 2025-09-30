package vn.edu.fpt.transitlink.fleet.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.edu.fpt.transitlink.fleet.enumeration.FuelType;

import java.util.UUID;

@Schema(description = "Request object for creating a new vehicle")
public record CreateVehicleRequest(
        @Schema(description = "Name of the vehicle", example = "Bus #1")
        @NotBlank(message = "Vehicle name is required")
        String name,

        @Schema(description = "License plate of the vehicle", example = "29A-12345")
        @NotBlank(message = "License plate is required")
        String licensePlate,

        @Schema(description = "Capacity of the vehicle (number of passengers)", example = "40")
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @Schema(description = "Fuel type of the vehicle", example = "DIESEL")
        @NotNull(message = "Fuel type is required")
        FuelType fuelType,

        @Schema(description = "Fuel consumption rate (liters per 100 km)", example = "25.5")
        @NotNull(message = "Fuel consumption rate is required")
        Float fuelConsumptionRate,

        @Schema(description = "ID of the depot where the vehicle is stationed", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "Depot ID is required")
        UUID depotId
) {}

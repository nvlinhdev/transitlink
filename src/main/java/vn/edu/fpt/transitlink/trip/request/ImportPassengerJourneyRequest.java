package vn.edu.fpt.transitlink.trip.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportPassengerJourneyRequest(
        @Schema(description = "Passenger email", example = "Email")
        @NotNull(message = "Column email is required")
        String email,

        @Schema(description = "Passenger first name", example = "First Name")
        @NotNull(message = "Column firstName is required")
        String firstName,

        @Schema(description = "Passenger last name", example = "Last Name")
        @NotNull(message = "Column lastName is required")
        String lastName,

        @Schema(description = "Passenger phone number", example = "Phone Number")
        String phoneNumber,

        @Schema(description = "Passenger Zalo phone number", example = "Zalo Phone Number")
        String zaloPhoneNumber,

        @Schema(description = "Pickup place name", example = "Pickup Place Name")
        @NotNull(message = "Column pickupPlaceName is required")
        String pickupPlaceName,

        @Schema(description = "Pickup latitude", example = "Pickup Latitude")
        @NotNull(message = "Column pickupLatitude is required")
        String pickupLatitude,

        @Schema(description = "Pickup longitude", example = "Pickup Longitude")
        @NotNull(message = "Column pickupLongitude is required")
        String pickupLongitude,

        @Schema(description = "Pickup address", example = "Pickup Address")
        @NotNull(message = "Column pickupAddress is required")
        String pickupAddress,

        @Schema(description = "Dropoff place name", example = "Dropoff Place Name")
        @NotNull(message = "Column dropoffPlaceName is required")
        String dropoffPlaceName,

        @Schema(description = "Dropoff latitude", example = "Dropoff Latitude")
        @NotNull(message = "Column dropoffLatitude is required")
        String dropoffLatitude,

        @Schema(description = "Dropoff longitude", example = "Dropoff Longitude")
        @NotNull(message = "Column dropoffLongitude is required")
        String dropoffLongitude,

        @Schema(description = "Dropoff address", example = "Dropoff Address")
        @NotNull(message = "Column dropoffAddress is required")
        String dropoffAddress,

        @Schema(description = "Trip date in format yyyy-MM-dd", example = "Latest Stop Arrival Time")
        @NotNull(message = "Column tripDate is required")
        String latestStopArrivalTime,

        @Schema(description = "Seat count", example = "Seat Count")
        @NotNull(message = "Column seatCount is required")
        String seatCount
) {
}

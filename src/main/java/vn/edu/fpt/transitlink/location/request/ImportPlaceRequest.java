package vn.edu.fpt.transitlink.location.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImportPlaceRequest(
        @NotBlank(message = "Place name cannot be blank")
        String name,
        @NotNull(message = "Latitude cannot be null")
        Double latitude,
        @NotNull(message = "Longitude cannot be null")
        Double longitude,
        @NotBlank(message = "Address cannot be blank")
        String address
) {
}

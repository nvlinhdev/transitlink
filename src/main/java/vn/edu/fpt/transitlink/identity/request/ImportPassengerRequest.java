package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import vn.edu.fpt.transitlink.identity.enumeration.Gender;

import java.time.LocalDate;

public record ImportPassengerRequest(
        // Account information
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "Email address for the account", example = "passenger@example.com")
        String email,

        @Schema(description = "Password for the account", example = "SecurePassword123!")
        String password,

        @NotBlank(message = "First name is required")
        @Schema(description = "First name of the passenger", example = "John")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Schema(description = "Last name of the passenger", example = "Doe")
        String lastName,

        @Schema(description = "Gender of the passenger", example = "MALE")
        Gender gender,

        @Schema(description = "Birth date of the passenger", example = "1990-05-15")
        LocalDate birthDate,

        @Schema(description = "Phone number of the passenger", example = "+84901234567")
        String phoneNumber,

        @Schema(description = "Zalo phone number of the passenger", example = "+84909876543")
        String zaloPhoneNumber,

        @Schema(description = "Avatar URL for the passenger", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        // Passenger-specific information
        @Schema(description = "Initial number of completed trips", example = "0")
        Integer totalCompletedTrips,

        @Schema(description = "Initial number of cancelled trips", example = "0")
        Integer totalCancelledTrips,

        @Schema(description = "Whether to send verification email", example = "true")
        Boolean sendVerificationEmail
) {
}

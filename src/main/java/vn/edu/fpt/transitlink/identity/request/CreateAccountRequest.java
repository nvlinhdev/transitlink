package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import vn.edu.fpt.transitlink.identity.enumeration.Gender;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.time.LocalDate;
import java.util.Set;

public record CreateAccountRequest(
        @Schema(example = "user@example.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @Schema(example = "Nguyen")
        @NotBlank(message = "First name cannot be blank")
        @Pattern(
                regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
                message = "First name is invalid. Only letters and spaces are allowed."
        )
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Schema(example = "Van A")
        @NotBlank(message = "Last name cannot be blank")
        @Pattern(
                regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
                message = "Last name is invalid. Only letters and spaces are allowed."
        )
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Schema(example = "MALE")
        Gender gender,

        @Schema(example = "1995-05-20")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @Schema(example = "0901234567")
        @Pattern(
                regexp = "^(\\+?84|0)\\d{9}$",
                message = "Phone number must start with 0 or +84 and have 10 digits"
        )
        String phoneNumber,

        @Schema(example = "0901234567")
        @Pattern(
                regexp = "^(\\+?84|0)\\d{9}$",
                message = "Phone number must start with 0 or +84 and have 10 digits"
        )
        String zaloPhoneNumber,

        @Schema(description = "User roles")
        @NotEmpty(message = "At least one role must be assigned")
        @NotNull(message = "Roles cannot be null")
        Set<RoleName> roles
) {
}

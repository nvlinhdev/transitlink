package vn.edu.fpt.transitlink.auth.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import vn.edu.fpt.transitlink.auth.enumeration.Gender;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
        )
        String password,
        @NotBlank(message = "First name cannot be blank")
        @Pattern(
                regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
                message = "First name is invalid. Only letters and spaces are allowed."
        )
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        @Pattern(
                regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
                message = "Last name is invalid. Only letters and spaces are allowed."
        )
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
        @NotNull(message = "Gender cannot be null")
        @Enumerated(EnumType.STRING)
        Gender gender,
        @Past(message = "Birth date must be in the past")
        @NotNull(message = "Birth date cannot be null")
        LocalDate birthDate,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
        String phoneNumber,
        @NotBlank(message = "Zalo phone number is required")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
        String zaloPhoneNumber,
        @URL(message = "Invalid URL format")
        @NotBlank(message = "Avatar URL cannot be blank")
        String avatarUrl
) {
}
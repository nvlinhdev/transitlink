package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import vn.edu.fpt.transitlink.identity.enumeration.Gender;

import java.time.LocalDate;

public record UpdateCurrentUserRequest(
    @Schema(example = "Nguyen")
    @Pattern(
            regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
            message = "First name is invalid. Only letters and spaces are allowed."
    )
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @Schema(example = "Van A")
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

    @Schema(example = "+84901234567")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Schema(example = "+84909876543")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String zaloPhoneNumber,

    @Schema(example = "https://example.com/avatar.png")
    @URL(message = "Invalid URL format")
    String avatarUrl
) {}

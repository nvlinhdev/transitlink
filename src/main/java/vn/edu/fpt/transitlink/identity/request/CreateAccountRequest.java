package vn.edu.fpt.transitlink.identity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import vn.edu.fpt.transitlink.identity.enumeration.Gender;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.time.LocalDate;
import java.util.Set;

public record CreateAccountRequest (
    @Schema(example = "linh.nguyen@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email,

    @Schema(example = "Password@123")
    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    String password,

    @Schema(example = "Linh")
    @NotBlank(message = "First name cannot be blank")
    @Pattern(
            regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
            message = "First name is invalid. Only letters and spaces are allowed."
    )
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @Schema(example = "Nguyen")
    @NotBlank(message = "Last name cannot be blank")
    @Pattern(
            regexp = "^[\\p{L}]+( [\\p{L}]+)*$",
            message = "Last name is invalid. Only letters and spaces are allowed."
    )
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @Schema(example = "MALE")
    @NotNull(message = "Gender cannot be null")
    Gender gender,

    @Schema(example = "1995-05-20")
    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date cannot be null")
    LocalDate birthDate,

    @Schema(example = "+84901234567")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Schema(example = "+84909876543")
    @NotBlank(message = "Zalo phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String zaloPhoneNumber,

    @Schema(example = "https://example.com/avatar.png")
    @URL(message = "Invalid URL format")
    @NotBlank(message = "Avatar URL cannot be blank")
    String avatarUrl,

    @Schema(description = "User roles")
    @NotNull(message = "Roles cannot be null")
    Set<RoleName> roles
) {}

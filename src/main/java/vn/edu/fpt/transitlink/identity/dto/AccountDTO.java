package vn.edu.fpt.transitlink.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.fpt.transitlink.identity.enumeration.Gender;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record AccountDTO(
    @Schema(example = "a573aa20-f56b-4888-8b5b-88a7ad21b928", description = "Unique identifier of the account")
    UUID id,

    @Schema(example = "user@example.com", description = "Email address of the account")
    String email,

    @Schema(example = "true", description = "Whether the email has been verified")
    Boolean emailVerified,

    @Schema(example = "true", description = "Whether the profile is completed")
    Boolean profileCompleted,

    @Schema(example = "Linh", description = "First name of the account holder")
    String firstName,

    @Schema(example = "Nguyen", description = "Last name of the account holder")
    String lastName,

    @Schema(example = "MALE", description = "Gender of the account holder")
    Gender gender,

    @Schema(example = "1995-05-20", description = "Birth date of the account holder")
    LocalDate birthDate,

    @Schema(example = "+84901234567", description = "Phone number of the account holder")
    String phoneNumber,

    @Schema(example = "+84909876543", description = "Zalo phone number of the account holder")
    String zaloPhoneNumber,

    @Schema(example = "https://example.com/avatar.png", description = "URL to the avatar image")
    String avatarUrl,

    @Schema(description = "Roles assigned to the account")
    Set<RoleDTO> roles
) {}

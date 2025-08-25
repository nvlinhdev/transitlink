package vn.edu.fpt.transitlink.identity.dto;

import vn.edu.fpt.transitlink.identity.enumeration.Gender;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record AccountDTO(
    UUID id,
    String email,
    Boolean emailVerified,
    String firstName,
    String lastName,
    Gender gender,
    LocalDate birthDate,
    String phoneNumber,
    String zaloPhoneNumber,
    String avatarUrl,
    Set<RoleDTO> roles
) {}

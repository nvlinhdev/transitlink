package vn.edu.fpt.transitlink.user.dto;

import vn.edu.fpt.transitlink.auth.entity.Gender;

import java.time.LocalDate;
import java.util.UUID;

public record UserProfileDTO(
        UUID id,
        UUID accountId,
        String firstName,
        String lastName,
        String phoneNumber,
        Gender gender,
        LocalDate dateOfBirth,
        String zaloPhoneNumber,
        String avatarUrl
) {}

package vn.edu.fpt.transitlink.profile.dto;

import vn.edu.fpt.transitlink.profile.entity.Gender;

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

package vn.edu.fpt.transitlink.profile.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePhoneNumberRequest(@NotBlank String firebasePhoneVerifiedToken) {
}

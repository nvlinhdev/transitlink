package vn.edu.fpt.transitlink.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePhoneNumberRequest(@NotBlank String firebasePhoneVerifiedToken) {
}

package vn.edu.fpt.transitlink.identity.request;


import vn.edu.fpt.transitlink.identity.enumeration.Gender;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.time.LocalDate;
import java.util.Set;

public record CreateAccountRequest (
    String email,
    String password,
    String firstName,
    String lastName,
    Gender gender,
    LocalDate birthDate,
    String phoneNumber,
    String zaloPhoneNumber,
    String avatarUrl,
    Set<RoleName> roles
) {}

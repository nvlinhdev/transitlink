package vn.edu.fpt.transitlink.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import vn.edu.fpt.transitlink.profile.entity.Gender;

import java.time.LocalDate;

@Schema(name = "CreateProfileRequest", description = "Request to create a user profile with Firebase phone verification")
public record CreateProfileRequest(

        // ^                 : Start of string
        // [\\p{L} .'-]{1,50} : Allows 1 to 50 characters including:
        //                     - \\p{L} : Any Unicode letter (supports international characters like â, ă, đ, etc.)
        //                     - space  : Space character
        //                     - .      : Period (e.g., used in initials like J.K. Rowling)
        //                     - '      : Apostrophe (e.g., O'Connor)
        //                     - -      : Hyphen (e.g., Jean-Pierre)
        // $                 : End of string
        @Pattern(
                regexp = "^[\\p{L} .'-]{1,50}$",
                message = "First name can only contain letters, spaces, dots, hyphens, and apostrophes (no numbers or special characters)"
        )
        @NotBlank(message = "First name must not be blank")
        @Schema(description = "First name of the user. Only letters, spaces, hyphens, apostrophes, and dots are allowed.", example = "Linh")
        String firstName,

        // ^                 : Start of string
        // [\\p{L} .'-]{1,50} : Allows 1 to 50 characters including:
        //                     - \\p{L} : Any Unicode letter (supports international characters like â, ă, đ, etc.)
        //                     - space  : Space character
        //                     - .      : Period (e.g., used in initials like J.K. Rowling)
        //                     - '      : Apostrophe (e.g., O'Connor)
        //                     - -      : Hyphen (e.g., Jean-Pierre)
        // $                 : End of string
        @Pattern(
                regexp = "^[\\p{L} .'-]{1,50}$",
                message = "Last name can only contain letters, spaces, dots, hyphens, and apostrophes (no numbers or special characters)"
        )
        @NotBlank(message = "Last name must not be blank")
        @Schema(description = "Last name of the user. Only letters, spaces, hyphens, apostrophes, and dots are allowed.", example = "Nguyen")
        String lastName,

        @NotNull
        @Schema(description = "Gender of the user", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
        Gender gender,

        @NotNull
        @Past
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Date of birth", example = "2000-01-15")
        LocalDate dateOfBirth,

        // Regex explanation:
        // ^             : Start of string
        // (\\+84|0)     : Starts with "+84" or "0"
        // \\d{9}      : Followed by exactly 9 digits
        // $             : End of string
        @Pattern(
                regexp = "^(\\+84|0)\\d{9}$",
                message = "Invalid Zalo phone number"
        )
        @NotBlank
        @Schema(description = "Zalo phone number (must start with +84 or 0, followed by 9 digits)", example = "0912345678")
        String zaloPhoneNumber,

        @URL
        @Schema(description = "Avatar image URL", example = "https://cdn.example.com/avatar.png")
        String avatarUrl,

        @NotBlank
        @Schema(description = "Firebase ID token obtained after phone number verification", example = "eyJhbGciOiJSUzI1...")
        String firebaseToken
) {}
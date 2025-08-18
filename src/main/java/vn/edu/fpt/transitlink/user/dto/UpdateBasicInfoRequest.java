package vn.edu.fpt.transitlink.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import vn.edu.fpt.transitlink.user.entity.Gender;

import java.time.LocalDate;

public record UpdateBasicInfoRequest(
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
        String lastName,

        @NotNull
        Gender gender,

        @NotNull
        @Past
        @JsonFormat(pattern = "yyyy-MM-dd")
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
        String zaloPhoneNumber,

        @URL
        String avatarUrl
) {
}

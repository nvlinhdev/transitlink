package vn.edu.fpt.transitlink.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationData {
    private String email;
    private String otp;
    private String name;
    private OffsetDateTime expiresAt;
    private int attempts;
    private boolean blocked;
}

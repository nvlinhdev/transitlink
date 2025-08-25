package vn.edu.fpt.transitlink.identity.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import vn.edu.fpt.transitlink.identity.enumeration.VerificationType;

import java.time.LocalDateTime;

@Builder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,       // Dùng fully-qualified class name
        include = JsonTypeInfo.As.PROPERTY, // Nhúng vào như một field
        property = "@class"                 // Tên field chứa type info
)
public record VerificationData(
        String email,
        String token,
        String otp,
        VerificationType type,
        LocalDateTime expiryTime
) {}

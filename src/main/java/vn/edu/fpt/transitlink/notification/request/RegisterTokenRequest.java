package vn.edu.fpt.transitlink.notification.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.edu.fpt.transitlink.notification.enumeration.DevicePlatform;

import java.util.UUID;

@Data
public class RegisterTokenRequest {
    @NotNull
    private UUID accountId;

    @NotBlank
    private String token;

    @NotNull
    private DevicePlatform platform;
}

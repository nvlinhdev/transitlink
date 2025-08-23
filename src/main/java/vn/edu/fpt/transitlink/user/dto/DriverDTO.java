package vn.edu.fpt.transitlink.user.dto;

import java.util.UUID;

public record DriverDTO(
        UUID id,
        String name,
        String phone,
        String licenseNumber,
        String status
) {

}

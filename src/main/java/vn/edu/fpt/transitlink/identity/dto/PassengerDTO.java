package vn.edu.fpt.transitlink.identity.dto;

import java.util.UUID;

public record PassengerDTO(
        UUID id,
        String name,
        String phone,
        String address,
        String note
) {

}

package vn.edu.fpt.transitlink.location.dto;

import java.util.UUID;

public record PassengerLocationMessage(
        UUID passengerId,
        Double latitude,
        Double longitude
) {}

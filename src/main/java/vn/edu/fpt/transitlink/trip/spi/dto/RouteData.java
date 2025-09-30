package vn.edu.fpt.transitlink.trip.spi.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RouteData {
    private UUID vehicleId;
    private Double estimatedDistanceKm;
    private Double estimatedDurationMin;
    private OffsetDateTime plannedDepartureTime;
    private OffsetDateTime  plannedArrivalTime;
    private String geometry;
    @Enumerated(EnumType.STRING)
    private RouteType type;
    private List<StopData> stops;
    private String directionUrl;
}

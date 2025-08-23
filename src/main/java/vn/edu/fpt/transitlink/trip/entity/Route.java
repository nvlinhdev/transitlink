package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
public class Route extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID Id;
    private UUID driverId;
    private UUID vehicleId;
    private String name;
    private float estimatedDistanceKm;
    private float estimatedDurationMin;
    private OffsetDateTime  plannedDepartureTime;
    private OffsetDateTime  checkinTime;
    private OffsetDateTime  plannedArrivalTime;
    private OffsetDateTime checkoutTime;
    private String status;
}

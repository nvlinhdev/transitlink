package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "routes")
public class Route extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID Id;
    private UUID driverId;
    private UUID vehicleId;
    private Double estimatedDistanceKm;
    private Double estimatedDurationMin;
    private OffsetDateTime  plannedDepartureTime;
    private OffsetDateTime  checkinTime;
    private OffsetDateTime  plannedArrivalTime;
    private OffsetDateTime checkoutTime;
    @Column(columnDefinition = "TEXT")
    private String geometry;
    @Column(columnDefinition = "TEXT")
    private String directionUrl;
    @Enumerated(EnumType.STRING)
    private RouteType type;
    @Enumerated(EnumType.STRING)
    private RouteStatus status;
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sequence ASC")
    private List<Stop> stops;
}

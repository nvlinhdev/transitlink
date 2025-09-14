package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "passenger_journeys",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"passengerId", "pickupPlaceId", "dropoffPlaceId", "lastestStopArrivalTime"})
    }
)
public class PassengerJourney extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID passengerId;
    private UUID pickupPlaceId;
    private UUID dropoffPlaceId;
    private UUID routeId;
    private OffsetDateTime lastestStopArrivalTime; // thời gian mà xe chính sẽ đến điểm dừng cố định
    private OffsetDateTime actualPickupTime;
    private OffsetDateTime actualDropoffTime;
    private Integer seatCount;
    @Enumerated(EnumType.STRING)
    private JourneyStatus status;
}

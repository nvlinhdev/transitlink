package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "passenger_journeys",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"passengerId", "pickupPlaceId", "dropoffPlaceId", "mainStopArrivalTime"})
    }
)
public class PassengerJourney extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID passengerId;
    private UUID pickupPlaceId;
    private UUID dropoffPlaceId;
    private OffsetDateTime mainStopArrivalTime;
    private Integer seatCount;
    @Enumerated(EnumType.STRING)
    private JourneyType journeyType;
    @Column(columnDefinition = "TEXT")
    private String geometry;
    private OffsetDateTime plannedPickupTime;
    private OffsetDateTime actualPickupTime;
    private OffsetDateTime plannedDropoffTime;
    private OffsetDateTime actualDropoffTime;
    @Enumerated(EnumType.STRING)
    private JourneyStatus status;
    @OneToMany(mappedBy = "passengerJourney")
    private List<StopJourneyMapping> stopJourneyMappings;
}

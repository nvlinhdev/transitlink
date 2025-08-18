package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
public class PassengerLocation extends BaseSoftDeletableEntity {
    @jakarta.persistence.Id
    @GeneratedValue
    private UUID Id;
    private UUID passengerId;
    private UUID pickupPlace;
    private UUID dropoffPlace;
    private UUID routeId;
    private OffsetDateTime lastestStopArrivalTime;
    private OffsetDateTime actualPickupTime;
    private OffsetDateTime actualDropoffTime;
    private int seatCount;
    private String status;

}

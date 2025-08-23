package vn.edu.fpt.transitlink.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.awt.*;
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
    @Id
    @GeneratedValue
    private UUID passengerLocationId;
    private UUID passengerId;
    private Point location;
    private int recordAt;
    private String status;

}

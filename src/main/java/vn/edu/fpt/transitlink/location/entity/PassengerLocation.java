package vn.edu.fpt.transitlink.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "passenger_locations")
public class PassengerLocation {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID passengerId;
    private OffsetDateTime recordAt;
    private Double latitude;
    private Double longitude;
}

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
@Table(name = "driver_locations")
public class DriverLocation {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID driverId;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime recordAt;
}

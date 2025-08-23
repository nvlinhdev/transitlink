package vn.edu.fpt.transitlink.navigation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
public class DriverLocation extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String driverId;
    private Double lat;
    private Double lng;
    private OffsetDateTime timestamp;
}

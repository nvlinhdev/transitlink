package vn.edu.fpt.transitlink.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.awt.*;
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
public class DriverLocation extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID Id;
    private UUID driverId;
    private OffsetDateTime recordAt;
    private Point location;
    private Float speedKmH;
    private Float headingDeg;
    private String status;


}

package vn.edu.fpt.transitlink.monitoring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
public class MonitoringDashboard extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
//    private UUID id;
    private int activeTrips;
    private int activeDrivers;
    private int alerts;
}

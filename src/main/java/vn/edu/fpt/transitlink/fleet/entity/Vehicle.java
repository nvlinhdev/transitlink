package vn.edu.fpt.transitlink.fleet.entity;


import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.fleet.enumeration.FuelType;
import vn.edu.fpt.transitlink.fleet.enumeration.VehicleStatus;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String licensePlate;
    private Integer capacity;
    private FuelType fuelType;
    private Float fuelConsumptionRate;
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    private UUID depotId;
}

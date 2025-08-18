package vn.edu.fpt.transitlink.fleet.entity;


import jakarta.persistence.*;
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
@Table(
        name = "depots",
        uniqueConstraints = @UniqueConstraint(columnNames = "depot_id")
)
public class Vehicle extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String license_plate;
    private String vehicle_type;
    private int capacity;
    private String fuel_type;
    private double fuel_consumption_rate;
    private String status;
    }

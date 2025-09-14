package vn.edu.fpt.transitlink.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "passengers")
public class Passenger extends BaseSoftDeletableEntity {
    @Id
    private UUID id;
    private Integer totalCompletedTrips;
    private Integer totalCancelledTrips;
    private UUID accountId;
}

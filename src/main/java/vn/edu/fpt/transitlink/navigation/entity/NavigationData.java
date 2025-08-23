package vn.edu.fpt.transitlink.navigation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class NavigationData extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tripId;
    private String route;
    private OffsetDateTime estimatedArrivalTime;
}

package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;
import vn.edu.fpt.transitlink.shared.base.BaseTimeAuditableEntity;

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
public class TripCheckin extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID triId;
    private UUID passengerId;
    private OffsetDateTime time;
}

package vn.edu.fpt.transitlink.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.util.Date;
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
        name = "trip",
        uniqueConstraints = @UniqueConstraint(columnNames = "trip_id")
)
public class Schedule extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String routeId;
    private Date date;

}

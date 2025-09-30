package vn.edu.fpt.transitlink.trip.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stops")
public class Stop extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;
    private Double latitude;
    private Double longitude;
    private Integer sequence;
    private OffsetDateTime plannedDepartureTime;
    private OffsetDateTime actualDepartureTime;
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StopJourneyMapping> stopJourneyMappings;
}

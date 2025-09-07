package vn.edu.fpt.transitlink.location.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "places",
        uniqueConstraints = @UniqueConstraint(columnNames = {"latitude", "longitude"})
)
public class Place {
    @Id
    private UUID id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
}

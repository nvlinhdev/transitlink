package vn.edu.fpt.transitlink.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.identity.enumeration.LincenseClass;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "drivers", uniqueConstraints = {
    @UniqueConstraint(name = "uk_driver_account_id", columnNames = {"account_id"})
})
public class Driver extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String licenseNumber;
    @Enumerated(EnumType.STRING)
    private LincenseClass licenseClass;
    private UUID accountId;
    private UUID depotId;
}

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
@Table(name = "drivers")
public class Driver extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String licenseNumber;
    private LincenseClass lincenseClass;
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private UUID depotId;
}

package vn.edu.fpt.transitlink.user.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.time.LocalDate;
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
        name = "profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = "account_id")
)
public class UserProfile extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "account_id", nullable = false, unique = true)
    private UUID accountId;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "phone_number", length = 14, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;


    @Column(name = "zalo_phone_number", length = 14, nullable = false)
    private String zaloPhoneNumber;

    @Column(name = "avatar_url")
    private String avatarUrl;
}

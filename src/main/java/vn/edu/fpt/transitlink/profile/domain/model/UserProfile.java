package vn.edu.fpt.transitlink.profile.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = "accountId")
)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserProfile {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, unique = true)
    private UUID accountId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String phoneNumber;
    private String gender;
    private String zaloPhoneNumber;
    private String avatarUrl;
}

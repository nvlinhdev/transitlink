package vn.edu.fpt.transitlink.identity.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.transitlink.identity.enumeration.Gender;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"phone_number"})
        })
public class Account extends BaseSoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    private Boolean emailVerified;
    private String password;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthDate;
    private String phoneNumber;
    private String zaloPhoneNumber;
    private String avatarUrl;
    @ManyToMany(fetch =  FetchType.EAGER)
    @JoinTable(name = "account_role_mapping",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private Set<Role> roles;
    @OneToOne(mappedBy = "account")
    private Driver driver;
    @OneToOne(mappedBy = "account")
    private Passenger passenger;
}

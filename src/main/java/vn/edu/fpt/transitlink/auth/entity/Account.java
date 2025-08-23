package vn.edu.fpt.transitlink.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.transitlink.shared.base.BaseSoftDeletableEntity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username"}),
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"phone_number"}),
                @UniqueConstraint(columnNames = {"zalo_phone_number"})
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
    private Gender gender;
    private LocalDate birthDate;
    private String phoneNumber;
    private String zaloPhoneNumber;
    private String avatarUrl;
    @ManyToMany(fetch =  FetchType.EAGER)
    @JoinTable(name = "account_role_mapping",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}

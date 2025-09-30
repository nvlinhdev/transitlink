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
    private Boolean profileCompleted;
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

    // Setter có logic normalize
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = normalizeVietnamPhone(phoneNumber);
    }

    public void setZaloPhoneNumber(String zaloPhoneNumber) {
        this.zaloPhoneNumber = normalizeVietnamPhone(zaloPhoneNumber);
    }

    private String normalizeVietnamPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String cleaned = phone.replaceAll("[^0-9+]", "");
        if (cleaned.startsWith("+84")) {
            return cleaned;
        } else if (cleaned.startsWith("84")) {
            return "+" + cleaned;
        } else if (cleaned.startsWith("0")) {
            return "+84" + cleaned.substring(1);
        } else {
            return "+84" + cleaned;
        }
    }

    public boolean isProfileCompleted() {
        return phoneNumber != null && zaloPhoneNumber != null;
    }
}

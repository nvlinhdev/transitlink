package vn.edu.fpt.transitlink.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
    @Column(nullable = false, unique = true, length = 512)
    private String token;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @Column(name = "revoked")
    private Boolean revoked = false;

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt);
    }
}
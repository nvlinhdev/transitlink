package vn.edu.fpt.transitlink.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class CustomUserPrincipal implements UserDetails, OidcUser {

    private final UUID id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private final Map<String, Object> attributes;

    public CustomUserPrincipal(UUID id,
                               String email,
                               String password,
                               Collection<? extends GrantedAuthority> authorities,
                               OidcIdToken idToken,
                               OidcUserInfo userInfo,
                               Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.idToken = idToken;
        this.userInfo = userInfo;
        this.attributes = attributes;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    // ===== UserDetails methods =====
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password; // OIDC login không dùng password local
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ===== OidcUser methods =====
    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return email;
    }
}

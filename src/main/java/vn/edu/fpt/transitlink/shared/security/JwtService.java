package vn.edu.fpt.transitlink.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.shared.config.AppProperties;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class JwtService {

    private final AppProperties props;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(props.security().jwt().secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // -------------------- Generate Token --------------------
    public String generateAccessToken(CustomUserPrincipal userPrincipal) {
        return generateToken(userPrincipal, props.security().jwt().accessToken().expiration());
    }

    public String generateRefreshToken(CustomUserPrincipal userPrincipal) {
        return generateToken(userPrincipal, props.security().jwt().refreshToken().expiration());
    }

    private String generateToken(CustomUserPrincipal userPrincipal, long expiration) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId().toString())
                .claim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // -------------------- Extract Claims --------------------
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        String idStr = extractClaim(token, claims -> claims.get("id", String.class));
        return UUID.fromString(idStr);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        Object authoritiesClaim = claims.get("authorities");

        if (authoritiesClaim instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // -------------------- Validate --------------------
    public boolean isTokenValid(String token, CustomUserPrincipal userPrincipal) {
        String username = extractUsername(token);
        return username.equals(userPrincipal.getUsername())
                && extractExpiration(token).after(new Date());
    }

    public long getAccessTokenExpiration() {
        return props.security().jwt().accessToken().expiration();
    }

    public long getRefreshTokenExpiration() {
        return props.security().jwt().refreshToken().expiration();
    }
}

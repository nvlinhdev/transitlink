package vn.edu.fpt.transitlink.auth.service;

import vn.edu.fpt.transitlink.auth.entity.RefreshToken;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(CustomUserPrincipal userDetails);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByToken(String token);
}
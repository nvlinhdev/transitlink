package vn.edu.fpt.transitlink.auth.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.auth.entity.Account;
import vn.edu.fpt.transitlink.auth.entity.RefreshToken;
import vn.edu.fpt.transitlink.auth.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.auth.repository.AccountRepository;
import vn.edu.fpt.transitlink.auth.repository.RefreshTokenRepository;
import vn.edu.fpt.transitlink.auth.security.JwtService;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;
import vn.edu.fpt.transitlink.auth.service.RefreshTokenService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.time.OffsetDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
@Transactional
public class RefreshTokenImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final AccountRepository accountRepository;

    private final JwtService jwtService;

    @Override
    public RefreshToken createRefreshToken(CustomUserPrincipal userPrincipal) {
        Account account = accountRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found for email: " + userPrincipal.getUsername()));



        refreshTokenRepository.deleteByAccount(account);

        String jwtToken = jwtService.generateRefreshToken(userPrincipal);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusNanos(jwtService.getRefreshTokenExpiration() * 1000000));
        refreshToken.setToken(jwtToken);

        return refreshTokenRepository.save(refreshToken);
    }
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN, "Refresh token is expired or invalid");
        }
        return token;
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}

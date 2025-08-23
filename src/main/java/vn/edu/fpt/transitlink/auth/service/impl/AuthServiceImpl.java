package vn.edu.fpt.transitlink.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.auth.dto.LoginRequest;
import vn.edu.fpt.transitlink.auth.dto.RegisterRequest;
import vn.edu.fpt.transitlink.auth.dto.TokenData;
import vn.edu.fpt.transitlink.auth.entity.Account;
import vn.edu.fpt.transitlink.auth.entity.RefreshToken;
import vn.edu.fpt.transitlink.auth.entity.Role;
import vn.edu.fpt.transitlink.auth.entity.RoleName;
import vn.edu.fpt.transitlink.auth.exception.AuthErrorCode;
import vn.edu.fpt.transitlink.auth.repository.AccountRepository;
import vn.edu.fpt.transitlink.auth.repository.RoleRepository;
import vn.edu.fpt.transitlink.auth.security.userdetails.CustomUserDetailsService;
import vn.edu.fpt.transitlink.auth.security.JwtService;
import vn.edu.fpt.transitlink.auth.service.GoogleTokenVerifierService;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;
import vn.edu.fpt.transitlink.auth.service.AuthService;
import vn.edu.fpt.transitlink.auth.service.EmailVerificationService;
import vn.edu.fpt.transitlink.auth.service.RefreshTokenService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private final EmailVerificationService emailVerificationService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    @Override
    public boolean register(RegisterRequest registerRequest) {
        if (accountRepository.existsByEmail(registerRequest.email())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Account account = new Account();
        account.setEmail(registerRequest.email());
        account.setPassword(passwordEncoder.encode(registerRequest.password()));
        account.setFirstName(registerRequest.firstName());
        Role role = roleRepository.findByName(RoleName.PASSENGER)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
        account.setRoles(Set.of(role));
        accountRepository.save(account);
        // Send verification email
        emailVerificationService.sendVerificationEmail(account.getEmail(), account.getFirstName(), account.getLastName());
        return true;
    }

    @Override
    public TokenData login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.identifier(),
                            loginRequest.password()
                    )
            );
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal);
            return buildTokenData(userPrincipal, refreshToken);

        } catch (BadCredentialsException e) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS, e);
        }
    }

    @Override
    public TokenData refresh(String requestRefreshToken){
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

            refreshTokenService.verifyExpiration(refreshToken);

            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) userDetailsService.loadUserByUsername(refreshToken.getAccount().getEmail());
            return buildTokenData(userPrincipal, refreshToken);

        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN, e);
        }
    }


    @Override
    public void logout(String refreshToken){
        refreshTokenService.deleteByToken(refreshToken);
    }

    private TokenData buildTokenData(CustomUserPrincipal userPrincipal, RefreshToken refreshToken) {
        String accessToken = jwtService.generateAccessToken(userPrincipal);
        return TokenData.from(
                accessToken,
                refreshToken.getToken(),
                jwtService.getAccessTokenExpiration()
        );
    }

    @Override
    public TokenData loginWithGoogleMobile(String idTokenString) {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verify(idTokenString);
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String avatarUrl = (String) payload.get("picture");
        Boolean emailVerified = payload.getEmailVerified();

        accountRepository.findByEmail(email)
                .orElseGet(() -> {
                    Account newAcc = new Account();
                    newAcc.setEmail(email);
                    newAcc.setFirstName(firstName);
                    newAcc.setLastName(lastName);
                    newAcc.setAvatarUrl(avatarUrl);
                    newAcc.setEmailVerified(emailVerified != null && emailVerified);
                    Role role = roleRepository.findByName(RoleName.PASSENGER)
                            .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
                    newAcc.setRoles(Set.of(role));
                    return accountRepository.save(newAcc);
                });

        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) userDetailsService.loadUserByUsername(email);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal);
        return buildTokenData(userPrincipal, refreshToken);
    }

}

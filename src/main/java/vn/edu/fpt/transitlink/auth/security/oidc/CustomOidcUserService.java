package vn.edu.fpt.transitlink.auth.security.oidc;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.auth.entity.Account;
import vn.edu.fpt.transitlink.auth.entity.Role;
import vn.edu.fpt.transitlink.auth.entity.RoleName;
import vn.edu.fpt.transitlink.auth.exception.AuthErrorCode;
import vn.edu.fpt.transitlink.auth.repository.AccountRepository;
import vn.edu.fpt.transitlink.auth.repository.RoleRepository;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
@Transactional
public class CustomOidcUserService extends OidcUserService {
    private AccountRepository accountRepository;
    private RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();

        String email = (String) attributes.get("email");
        Account account = accountRepository.findByEmail(email)
                .or(() -> {
                    String firstName = (String) attributes.get("given_name");
                    String lastName = (String) attributes.get("family_name");
                    String avatarUrl = (String) attributes.get("picture");
                    Boolean emailVerified = (Boolean) attributes.get("email_verified");

                    Account newAccount = new Account();
                    newAccount.setEmail(email);
                    newAccount.setEmailVerified(true);
                    newAccount.setFirstName(firstName);
                    newAccount.setLastName(lastName);
                    newAccount.setAvatarUrl(avatarUrl);
                    newAccount.setEmailVerified(emailVerified != null && emailVerified);

                    Optional<Role> passenger = roleRepository.findByName(RoleName.PASSENGER);
                    if (passenger.isEmpty()) {
                        throw new OAuth2AuthenticationException("Passenger role not found");
                    }

                    newAccount.setRoles(Set.of(passenger.get()));

                    return Optional.of(accountRepository.save(newAccount));
                }).orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND, "User not found with email: " + email));

        return new CustomUserPrincipal(
                account.getId(),
                account.getEmail(),
                null,
                account.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .toList(),
                null,
                null,
                null
        );
    }
}

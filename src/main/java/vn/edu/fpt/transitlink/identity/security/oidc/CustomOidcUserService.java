package vn.edu.fpt.transitlink.identity.security.oidc;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Passenger;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.repository.PassengerRepository;
import vn.edu.fpt.transitlink.identity.repository.RoleRepository;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class CustomOidcUserService extends OidcUserService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PassengerRepository passengerRepository;

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

                    Account newAccount = new Account();
                    newAccount.setEmail(email);
                    newAccount.setEmailVerified(true);
                    newAccount.setFirstName(firstName);
                    newAccount.setLastName(lastName);
                    newAccount.setAvatarUrl(avatarUrl);
                    newAccount.setEmailVerified(true);
                    newAccount.setProfileCompleted(false);

                    Optional<Role> passenger = roleRepository.findByName(RoleName.PASSENGER);
                    if (passenger.isEmpty()) {
                        throw new OAuth2AuthenticationException("Passenger role not found");
                    }

                    newAccount.setRoles(Set.of(passenger.get()));

                    Optional<Account> otpAccount = Optional.of(accountRepository.save(newAccount));

                    Passenger passengerEntity = new Passenger();
                    passengerEntity.setAccount(otpAccount.get());
                    passengerEntity.setTotalCompletedTrips(0);
                    passengerEntity.setTotalCancelledTrips(0);
                    passengerRepository.save(passengerEntity);

                    return otpAccount;

                }).orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND, "User not found with email: " + email));

        return new CustomUserPrincipal(
                account.getId(),
                account.getEmail(),
                account.getEmailVerified(),
                account.getProfileCompleted(),
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

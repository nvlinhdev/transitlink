package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.transitlink.identity.request.RegisterRequest;
import vn.edu.fpt.transitlink.identity.request.ResendVerificationRequest;
import vn.edu.fpt.transitlink.identity.request.VerifyEmailRequest;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Passenger;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.identity.enumeration.VerificationType;
import vn.edu.fpt.transitlink.identity.mapper.RoleMapper;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.repository.PassengerRepository;
import vn.edu.fpt.transitlink.identity.service.RegistrationService;
import vn.edu.fpt.transitlink.identity.service.RoleService;
import vn.edu.fpt.transitlink.identity.service.VerificationService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.HashMap;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final AccountRepository accountRepository;
    private final PassengerRepository passengerRepository;
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;


    @Transactional
    @Override
    public boolean register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Account account = new Account();
        account.setEmail(request.email());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setFirstName(request.firstName());
        account.setLastName(request.lastName());
        account.setPhoneNumber(request.phoneNumber());
        account.setEmailVerified(false);
        account.setGender(request.gender());
        account.setBirthDate(request.birthDate());
        account.setAvatarUrl(request.avatarUrl());
        account.setZaloPhoneNumber(request.zaloPhoneNumber());
        Role role = roleMapper.toEntity(roleService.findByName(RoleName.PASSENGER));
        account.setRoles(Set.of(role));
        accountRepository.save(account);

        Passenger passenger = new Passenger();
        passenger.setAccount(account);
        passenger.setTotalCompletedTrips(0);
        passenger.setTotalCancelledTrips(0);
        passengerRepository.save(passenger);

        HashMap<String, Object> templateVars = new HashMap<>();
        templateVars.put("fullName", account.getFirstName() + " " + account.getLastName());
        // Send verification email
        verificationService.sendVerificationEmail(
                account.getEmail(),
                VerificationType.ACCOUNT_ACTIVATION,
                templateVars
        );
        return true;
    }


    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        if (Boolean.TRUE.equals(account.getEmailVerified())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        boolean verified = switch (request.method()) {
            case OTP -> verificationService.verifyOtp(request.email(), request.otp(), VerificationType.ACCOUNT_ACTIVATION);
            case LINK -> verificationService.verifyToken(request.token(), VerificationType.ACCOUNT_ACTIVATION);
        };

        if (!verified) {
            throw switch (request.method()) {
                case OTP -> new BusinessException(AuthErrorCode.INVALID_OTP);
                case LINK -> new BusinessException(AuthErrorCode.INVALID_VERIFICATION_TOKEN);
            };
        }

        account.setEmailVerified(true);
        accountRepository.save(account);
        return true;
    }

    @Override
    public void resendVerificationEmail(ResendVerificationRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        if (Boolean.TRUE.equals(account.getEmailVerified())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        HashMap<String, Object> templateVars = new HashMap<>();
        templateVars.put("fullName", account.getFirstName() + " " + account.getLastName());
        verificationService.sendVerificationEmail(
                account.getEmail(),
                VerificationType.ACCOUNT_ACTIVATION,
                templateVars
        );
    }
}

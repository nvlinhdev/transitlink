package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.dto.ChangePasswordRequest;
import vn.edu.fpt.transitlink.identity.dto.ForgotPasswordRequest;
import vn.edu.fpt.transitlink.identity.dto.ResetPasswordRequest;
import vn.edu.fpt.transitlink.identity.dto.SetPasswordRequest;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.identity.enumeration.VerificationType;
import vn.edu.fpt.transitlink.identity.repository.AccountRepository;
import vn.edu.fpt.transitlink.identity.service.PasswordService;
import vn.edu.fpt.transitlink.identity.service.VerificationService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class PasswordServiceImpl implements PasswordService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    @Override
    public void sendResetPasswordEmail(ForgotPasswordRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        HashMap<String, Object> templateVars = new HashMap<>();
        templateVars.put("fullName", account.getFirstName() + " " + account.getLastName());

        verificationService.sendVerificationEmail(
                account.getEmail(),
                VerificationType.PASSWORD_RESET,
                templateVars
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));
        boolean isValid = switch (request.method()){
            case OTP -> verificationService.verifyOtp(request.email(), request.otp(), VerificationType.PASSWORD_RESET);
            case LINK -> verificationService.verifyToken(request.token(), VerificationType.PASSWORD_RESET);
        };
        if (!isValid) {
            throw switch (request.method()){
                case OTP -> new BusinessException(AuthErrorCode.INVALID_OTP);
                case LINK -> new BusinessException(AuthErrorCode.INVALID_VERIFICATION_TOKEN);
            };
        }
        account.setPassword(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(account);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getPassword() == null || account.getPassword().isEmpty()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_HAS_NO_PASSWORD);
        }
        if (!passwordEncoder.matches(request.oldPassword(), account.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_OLD_PASSWORD);
        }
        account.setPassword(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(account);
    }

    @Override
    public void setPassword(String email, SetPasswordRequest request) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_ALREADY_HAS_PASSWORD);
        }
        account.setPassword(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(account);
    }
}

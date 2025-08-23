package vn.edu.fpt.transitlink.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.transitlink.auth.dto.ResendVerificationRequest;
import vn.edu.fpt.transitlink.auth.dto.VerifyEmailRequest;
import vn.edu.fpt.transitlink.auth.entity.Account;
import vn.edu.fpt.transitlink.auth.exception.AuthErrorCode;
import vn.edu.fpt.transitlink.auth.dto.EmailVerificationData;
import vn.edu.fpt.transitlink.auth.repository.AccountRepository;
import vn.edu.fpt.transitlink.auth.service.EmailVerificationService;
import vn.edu.fpt.transitlink.mail_sender.service.MailService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private final ObjectMapper objectMapper;

    @Value("${app.email-verification.otp-expiration-minutes:10}")
    private int otpExpirationMinutes;

    @Value("${app.email-verification.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.email-verification.rate-limit-minutes:1}")
    private int rateLimitMinutes;

    @Value("${app.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    private static final String OTP_PREFIX = "email_otp:";
    private static final String TOKEN_PREFIX = "email_token:";
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Override
    @Transactional
    public void sendVerificationEmail(String email, String firstName, String lastName) {
        // Check rate limiting
        String rateLimitKey = RATE_LIMIT_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            throw new BusinessException(AuthErrorCode.TOO_MANY_REQUESTS);
        }

        // Generate OTP and verification token
        String otp = generateOtp();
        String verificationToken = UUID.randomUUID().toString();

        // Create verification data
        EmailVerificationData verificationData = new EmailVerificationData();
        verificationData.setEmail(email);
        verificationData.setOtp(otp);
        verificationData.setName(firstName + " " + lastName);
        verificationData.setExpiresAt(OffsetDateTime.now().plusMinutes(otpExpirationMinutes));
        verificationData.setAttempts(0);
        verificationData.setBlocked(false);

        // Store in Redis
        String otpKey = OTP_PREFIX + email;
        String tokenKey = TOKEN_PREFIX + verificationToken;

        redisTemplate.opsForValue().set(otpKey, verificationData, otpExpirationMinutes, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(tokenKey, email, otpExpirationMinutes, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(rateLimitKey, "blocked", rateLimitMinutes, TimeUnit.MINUTES);

        // Send email
        sendOtpEmail(email, firstName, otp, verificationToken);
    }

    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailRequest request) {
        String otpKey = OTP_PREFIX + request.email();
        Object value = redisTemplate.opsForValue().get(otpKey);
        if (value == null) {
            throw new BusinessException(AuthErrorCode.INVALID_OR_EXPIRED_OTP);
        }
        EmailVerificationData verificationData = objectMapper.convertValue(value, EmailVerificationData.class);

        if (verificationData == null) {
            throw new BusinessException(AuthErrorCode.INVALID_OR_EXPIRED_OTP);
        }

        if (verificationData.isBlocked()) {
            throw new BusinessException(AuthErrorCode.OTP_BLOCKED);
        }

        if (OffsetDateTime.now().isAfter(verificationData.getExpiresAt())) {
            redisTemplate.delete(otpKey);
            throw new BusinessException(AuthErrorCode.INVALID_OR_EXPIRED_OTP);
        }

        if (!verificationData.getOtp().equals(request.otp())) {
            // Increment attempts
            verificationData.setAttempts(verificationData.getAttempts() + 1);

            if (verificationData.getAttempts() >= maxAttempts) {
                verificationData.setBlocked(true);
                redisTemplate.opsForValue().set(otpKey, verificationData, otpExpirationMinutes, TimeUnit.MINUTES);
                throw new BusinessException(AuthErrorCode.OTP_BLOCKED);
            }

            redisTemplate.opsForValue().set(otpKey, verificationData, otpExpirationMinutes, TimeUnit.MINUTES);
            throw new BusinessException(AuthErrorCode.INVALID_OR_EXPIRED_OTP);
        }

        // Verify email in database
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        if (Boolean.TRUE.equals(account.getEmailVerified())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        account.setEmailVerified(true);
        accountRepository.save(account);

        // Clean up Redis
        redisTemplate.delete(otpKey);

        return true;
    }

    @Override
    @Transactional
    public boolean verifyEmailByUrl(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        String email = (String) redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            throw new BusinessException(AuthErrorCode.INVALID_OR_EXPIRED_OTP);
        }

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        if (Boolean.TRUE.equals(account.getEmailVerified())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        account.setEmailVerified(true);
        accountRepository.save(account);

        // Clean up Redis
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(OTP_PREFIX + email);

        return true;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(ResendVerificationRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        if (Boolean.TRUE.equals(account.getEmailVerified())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        sendVerificationEmail(account.getEmail(), account.getFirstName(), account.getLastName());
    }

    private String generateOtp() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    private void sendOtpEmail(String email, String name, String otp, String verificationToken) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", name);
        model.put("otp", otp);
        model.put("optExpiration", otpExpirationMinutes);
        model.put("verificationUrl", frontendBaseUrl + "/verify-email?token=" + verificationToken);

        mailService.sendHtmlMail(
                email,
                "Xác thực Email TransitLink",
                "email-verification",
                model
        );
    }
}
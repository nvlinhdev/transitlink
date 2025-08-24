package vn.edu.fpt.transitlink.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.auth.dto.VerificationData;
import vn.edu.fpt.transitlink.auth.enumeration.VerificationType;
import vn.edu.fpt.transitlink.auth.service.VerificationService;
import vn.edu.fpt.transitlink.mail_sender.dto.EmailRequest;
import vn.edu.fpt.transitlink.mail_sender.service.AsyncEmailService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class VerificationServiceImpl implements VerificationService {

    private final AsyncEmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;  // Changed to use Object type

    @Value("${app.security.verification.otp-expiration-minutes:15}")
    private int tokenExpiryMinutes;

    @Value("${app.security.frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public CompletableFuture<Boolean> sendVerificationEmail(String email, VerificationType type, Map<String, Object> templateVariables) {
        // Generate OTP (6-digit number)
        String otp = generateOtp();

        // Generate token for URL verification
        String token = UUID.randomUUID().toString();

        // Create verification data
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);
        VerificationData verificationData = VerificationData.builder()
                .email(email)
                .token(token)
                .otp(otp)
                .type(type)
                .expiryTime(expiryTime)
                .build();

        // Store in Redis with two keys for lookup by either email or token
        String emailKey = createEmailKey(email, type);
        String tokenKey = createTokenKey(token, type);

        redisTemplate.opsForValue().set(emailKey, verificationData, Duration.ofMinutes(tokenExpiryMinutes));
        redisTemplate.opsForValue().set(tokenKey, verificationData, Duration.ofMinutes(tokenExpiryMinutes));

        // Send email with verification details
        return sendEmail(email, otp, token, type, templateVariables);
    }

    @Override
    public boolean verifyOtp(String email, String otp, VerificationType type) {
        String key = createEmailKey(email, type);
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return false;
        }

        VerificationData data = (VerificationData) value;  // Cast from Object to VerificationData

        if (data.expiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        boolean isValid = data.otp().equals(otp);
        if (isValid) {
            // Delete verification data after successful verification
            redisTemplate.delete(key);
            redisTemplate.delete(createTokenKey(data.token(), type));
        }

        return isValid;
    }

    @Override
    public boolean verifyToken(String token, VerificationType type) {
        Optional<VerificationData> dataOpt = getVerificationDataByToken(token, type);

        if (dataOpt.isEmpty()) {
            return false;
        }

        VerificationData data = dataOpt.get();

        if (data.expiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Delete verification data after successful verification
        redisTemplate.delete(createEmailKey(data.email(), data.type()));
        redisTemplate.delete(createTokenKey(token, type));

        return true;
    }


    private Optional<VerificationData> getVerificationDataByToken(String token, VerificationType type) {
        String tokenKey = createTokenKey(token, type);
        Object value = redisTemplate.opsForValue().get(tokenKey);

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of((VerificationData) value);  // Cast from Object to VerificationData
    }

    private String generateOtp() {
        // Generate a 6-digit OTP
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1, 1000000));
    }

    private CompletableFuture<Boolean> sendEmail(String email, String otp, String token, VerificationType type, Map<String, Object> additionalVariables) {
        // Create verification URL
        String verificationUrl = frontendBaseUrl + type.getUrlPath() + "?token=" + token;

        // Prepare template variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("otp", otp);
        variables.put("verificationUrl", verificationUrl);
        variables.put("expiryMinutes", tokenExpiryMinutes);

        if (additionalVariables != null) {
            variables.putAll(additionalVariables);
        }

        // Create email request
        EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .subject(type.getEmailSubject())
                .templateName(type.getEmailTemplate())
                .variables(variables)
                .build();

        // Send email asynchronously
        return emailService.sendEmailAsync(emailRequest);
    }

    private String createEmailKey(String email, VerificationType type) {
        return type.getOtpPrefix() + email;
    }

    private String createTokenKey(String token, VerificationType type) {
        return type.getTokenPrefix() + token;
    }
}
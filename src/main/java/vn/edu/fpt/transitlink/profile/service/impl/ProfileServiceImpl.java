package vn.edu.fpt.transitlink.profile.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.profile.entity.UserProfile;
import vn.edu.fpt.transitlink.profile.exception.ProfileErrorCode;
import vn.edu.fpt.transitlink.profile.mapper.UserProfileMapper;
import vn.edu.fpt.transitlink.profile.repository.UserProfileRepository;
import vn.edu.fpt.transitlink.profile.service.ProfileService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {
    private static final String REDIS_KEY_FORMAT = "phone-verification:%s:%d";
    private static final Duration REDIS_TTL = Duration.ofMinutes(5); // redis key time-to-live
    private static final long MAX_AUTH_AGE_SECONDS = 300;
    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;
    private final StringRedisTemplate redisTemplate;

    public ProfileServiceImpl(UserProfileRepository repository, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.mapper = UserProfileMapper.INSTANCE;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserProfileDTO createProfile(UUID accountId, CreateProfileRequest request) {
        if (repository.existsByAccountId(accountId)) {
            throw new BusinessException(ProfileErrorCode.PROFILE_ALREADY_EXISTS, "Profile already exists for account: " + accountId);
        }

        String phoneNumber = extractPhoneNumber(request.firebaseToken());

        UserProfile profile = UserProfile.builder()
                .accountId(accountId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(phoneNumber)
                .gender(request.gender())
                .dateOfBirth(request.dateOfBirth())
                .zaloPhoneNumber(request.zaloPhoneNumber())
                .avatarUrl(request.avatarUrl())
                .build();

        repository.save(profile);

        return mapper.toResponse(profile);
    }

    private String extractPhoneNumber(String firebaseToken) {
        try {
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);

            long authTime = extractAuthTime(token);
            validateAuthTime(authTime);

            String phoneNumber = (String) token.getClaims().get("phone_number");
            if (phoneNumber == null) {
                throw new BusinessException(ProfileErrorCode.PHONE_NUMBER_NOT_IN_TOKEN);
            }

            String redisKey = REDIS_KEY_FORMAT.formatted(token.getUid(), authTime);
            boolean notUsedYet = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(redisKey, "USED", REDIS_TTL)
            );

            if (!notUsedYet) {
                throw new BusinessException(ProfileErrorCode.PHONE_VERIFICATION_ALREADY_USED);
            }

            return phoneNumber;

        } catch (FirebaseAuthException e) {
            throw new ThirdPartyException(ProfileErrorCode.PHONE_VERIFICATION_FAILED, e.getMessage(), e.getCause());
        }
    }

    private long extractAuthTime(FirebaseToken token) {
        Object authTimeObj = token.getClaims().get("auth_time");
        if (authTimeObj == null) {
            throw new BusinessException(ProfileErrorCode.PHONE_VERIFICATION_TOKEN_INVALID);
        }
        return ((Number) authTimeObj).longValue();
    }

    private void validateAuthTime(long authTime) {
        long now = Instant.now().getEpochSecond();
        if ((now - authTime) > MAX_AUTH_AGE_SECONDS) {
            throw new BusinessException(ProfileErrorCode.PHONE_VERIFICATION_TOKEN_EXPIRED);
        }
    }
}

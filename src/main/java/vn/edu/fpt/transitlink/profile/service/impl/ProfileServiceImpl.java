package vn.edu.fpt.transitlink.profile.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.profile.entity.UserProfile;
import vn.edu.fpt.transitlink.profile.event.ProfileCreatedEvent;
import vn.edu.fpt.transitlink.profile.exception.ProfileErrorCode;
import vn.edu.fpt.transitlink.profile.mapper.UserProfileMapper;
import vn.edu.fpt.transitlink.profile.repository.UserProfileRepository;
import vn.edu.fpt.transitlink.profile.service.ProfileService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserProfileMapper mapper;
    private final UserProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileServiceImpl(UserProfileRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.mapper = UserProfileMapper.INSTANCE;
    }

    @Override
    public UserProfileDTO createProfile(UUID accountId, CreateProfileRequest request) {
        if (repository.existsByAccountId(accountId)) {
            throw new BusinessException(ProfileErrorCode.PROFILE_ALREADY_EXISTS, "Profile already exists for account: " + accountId);
        }

        UserProfile profile = new UserProfile(null, accountId, request.firstName(), request.lastName(), request.phoneNumber(), request.gender(), request.zaloPhoneNumber(), request.avatarUrl());
        repository.save(profile);

        // Publish domain event
        eventPublisher.publishEvent(new ProfileCreatedEvent(accountId));

        return mapper.toResponse(profile);
    }
}

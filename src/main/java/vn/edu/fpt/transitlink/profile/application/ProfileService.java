package vn.edu.fpt.transitlink.profile.application;

import vn.edu.fpt.transitlink.profile.domain.exception.ProfileErrorCode;
import vn.edu.fpt.transitlink.profile.presentation.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.presentation.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.profile.domain.event.ProfileCreatedEvent;
import vn.edu.fpt.transitlink.profile.domain.model.UserProfile;
import vn.edu.fpt.transitlink.profile.domain.repository.UserProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.UUID;

@Service
public class ProfileService {

    private final UserProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileService(UserProfileRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public UserProfileDTO createProfile(UUID accountId, CreateProfileRequest request) {
        if (repository.existsByAccountId(accountId)) {
            throw new BusinessException(ProfileErrorCode.PROFILE_ALREADY_EXISTS, "Profile already exists for account: " + accountId);
        }

        UserProfile profile = new UserProfile(null, accountId, request.firstName(), request.lastName(), request.phoneNumber(), request.gender(), request.zaloPhoneNumber(), request.avatarUrl());
        repository.save(profile);

        // Publish domain event
        eventPublisher.publishEvent(new ProfileCreatedEvent(accountId));

        return new UserProfileDTO(
                profile.getId(),
                profile.getAccountId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhoneNumber(),
                profile.getGender(),
                profile.getZaloPhoneNumber(),
                profile.getAvatarUrl()
        );
    }
}

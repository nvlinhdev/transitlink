package vn.edu.fpt.transitlink.profile.application;

import vn.edu.fpt.transitlink.profile.api.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.api.dto.UserProfileResponse;
import vn.edu.fpt.transitlink.profile.domain.event.ProfileCreatedEvent;
import vn.edu.fpt.transitlink.profile.domain.exception.ProfileAlreadyExistsException;
import vn.edu.fpt.transitlink.profile.domain.model.UserProfile;
import vn.edu.fpt.transitlink.profile.domain.repository.UserProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    private final UserProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileService(UserProfileRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public UserProfileResponse createProfile(UUID accountId, CreateProfileRequest request) {
        if (repository.existsByAccountId(accountId)) {
            throw new ProfileAlreadyExistsException(accountId);
        }

        UserProfile profile = new UserProfile(null,accountId, request.firstName(), request.lastName(), request.phoneNumber(), request.gender(), request.zaloUrl(), request.avatarUrl());
        repository.save(profile);

        // Publish domain event
        eventPublisher.publishEvent(new ProfileCreatedEvent(accountId));

        return new UserProfileResponse(
                profile.getId(),
                profile.getAccountId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhoneNumber(),
                profile.getGender(),
                profile.getZaloUrl(),
                profile.getAvatarUrl()
        );
    }
}

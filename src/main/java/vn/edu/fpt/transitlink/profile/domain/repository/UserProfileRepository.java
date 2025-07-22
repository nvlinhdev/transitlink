package vn.edu.fpt.transitlink.profile.domain.repository;

import vn.edu.fpt.transitlink.profile.domain.model.UserProfile;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository {
    Optional<UserProfile> findByAccountId(UUID accountId);
    Optional<UserProfile> findById(UUID id);
    void save(UserProfile profile);
    boolean existsByAccountId(UUID accountId);
}

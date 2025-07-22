package vn.edu.fpt.transitlink.profile.infrastructure.repository;

import vn.edu.fpt.transitlink.profile.domain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByAccountId(UUID accountId);
    boolean existsByAccountId(UUID accountId);
}

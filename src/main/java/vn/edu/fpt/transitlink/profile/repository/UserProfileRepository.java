package vn.edu.fpt.transitlink.profile.repository;

import org.springframework.stereotype.Repository;
import vn.edu.fpt.transitlink.profile.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByAccountId(UUID accountId);
    boolean existsByAccountId(UUID accountId);
}

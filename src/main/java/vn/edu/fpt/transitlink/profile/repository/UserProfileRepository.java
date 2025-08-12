package vn.edu.fpt.transitlink.profile.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.profile.entity.UserProfile;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends SoftDeletableRepository<UserProfile, UUID> {
    boolean existsByAccountId(UUID accountId);
    Optional<UserProfile> findByAccountId(UUID accountId);

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM UserProfile u WHERE u.deleted = true AND u.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

package vn.edu.fpt.transitlink.auth.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.auth.entity.Account;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends SoftDeletableRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Account acc WHERE acc.deleted = true AND acc.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

package vn.edu.fpt.transitlink.identity.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.transitlink.identity.entity.Driver;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends SoftDeletableRepository<Driver, UUID> {
    boolean existsByAccountId(UUID accountId);

    @Query("SELECT d FROM Driver d WHERE d.id = :id")
    Optional<Driver> findByIdIncludingDeleted(@Param("id") UUID id);

    @Query("SELECT d FROM Driver d WHERE d.isDeleted = true")
    Page<Driver> findAllDeleted(Pageable pageable);

    @Query("SELECT COUNT(d) FROM Driver d WHERE d.isDeleted = true")
    long countDeleted();

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Driver d WHERE d.isDeleted = true AND d.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

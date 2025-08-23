package vn.edu.fpt.transitlink.user.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.user.entity.Driver;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DriverRepository extends SoftDeletableRepository<Driver, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Driver d WHERE d.deleted = true AND d.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

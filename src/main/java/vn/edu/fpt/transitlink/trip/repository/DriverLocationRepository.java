package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.DriverLocation;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DriverLocationRepository extends SoftDeletableRepository<DriverLocation, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM DriverLocation d WHERE d.deleted = true AND d.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

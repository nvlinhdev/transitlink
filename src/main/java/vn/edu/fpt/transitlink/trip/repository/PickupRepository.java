package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.Pickup;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PickupRepository extends SoftDeletableRepository<Pickup, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Pickup p WHERE p.deleted = true AND p.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

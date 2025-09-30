package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.Stop;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface StopRepository extends SoftDeletableRepository<Stop, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Stop s WHERE s.isDeleted = true AND s.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

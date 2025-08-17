package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.Dropoff;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DropoffRepository extends SoftDeletableRepository<Dropoff, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Dropoff d WHERE d.deleted = true AND d.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

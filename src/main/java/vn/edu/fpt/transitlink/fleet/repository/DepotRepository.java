package vn.edu.fpt.transitlink.fleet.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.fleet.entity.Depot;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DepotRepository extends SoftDeletableRepository<Depot, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Depot d WHERE d.isDeleted = true AND d.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

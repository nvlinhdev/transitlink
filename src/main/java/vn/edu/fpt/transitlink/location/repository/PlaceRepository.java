package vn.edu.fpt.transitlink.location.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.location.entity.Place;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PlaceRepository extends SoftDeletableRepository<Place, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Place p WHERE p.isDeleted = true AND p.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

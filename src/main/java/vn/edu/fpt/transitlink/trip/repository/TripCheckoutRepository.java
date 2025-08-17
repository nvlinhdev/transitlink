package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.TripCheckout;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface TripCheckoutRepository extends SoftDeletableRepository<TripCheckout, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM TripCheckout t WHERE t.deleted = true AND t.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

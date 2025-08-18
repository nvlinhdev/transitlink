package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.PassengerLocation;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PassengerLocationRepository extends SoftDeletableRepository<PassengerLocation, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM PassengerLocation p WHERE p.deleted = true AND p.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

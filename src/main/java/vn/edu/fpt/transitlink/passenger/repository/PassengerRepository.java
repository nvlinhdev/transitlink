package vn.edu.fpt.transitlink.passenger.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.passenger.entity.Passenger;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PassengerRepository extends SoftDeletableRepository<Passenger, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Passenger p WHERE p.deleted = true AND p.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

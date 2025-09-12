package vn.edu.fpt.transitlink.identity.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.transitlink.identity.entity.Passenger;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends SoftDeletableRepository<Passenger, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Passenger p WHERE p.isDeleted = true AND p.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);

    // Find passenger by account ID
    Optional<Passenger> findByAccountId(UUID accountId);

    // Check if account already has a passenger
    boolean existsByAccountId(UUID accountId);

    // Find deleted passengers with pagination
    @Query("SELECT p FROM Passenger p WHERE p.isDeleted = true")
    Page<Passenger> findAllDeleted(Pageable pageable);

    // Count deleted passengers
    @Query("SELECT COUNT(p) FROM Passenger p WHERE p.isDeleted = true")
    long countDeleted();

    // Find passenger by ID including deleted ones
    @Query("SELECT p FROM Passenger p WHERE p.id = :id")
    Optional<Passenger> findByIdIncludingDeleted(@Param("id") UUID id);

    // Bulk check passengers by account IDs for import operations
    @Query("SELECT p FROM Passenger p WHERE p.accountId IN :accountIds AND p.isDeleted = false")
    List<Passenger> findByAccountIdsIn(@Param("accountIds") List<UUID> accountIds);
}

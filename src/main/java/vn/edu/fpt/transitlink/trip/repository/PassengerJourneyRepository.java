package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourney;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassengerJourneyRepository extends SoftDeletableRepository<PassengerJourney, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM PassengerJourney p WHERE p.isDeleted = true AND p.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);

    // Find deleted journeys with pagination
    @Query("SELECT pj FROM PassengerJourney pj WHERE pj.isDeleted = true")
    Page<PassengerJourney> findAllDeleted(Pageable pageable);

    // Count deleted journeys
    @Query("SELECT COUNT(pj) FROM PassengerJourney pj WHERE pj.isDeleted = true")
    long countDeleted();

    // Find journey by ID including deleted ones
    @Query("SELECT pj FROM PassengerJourney pj WHERE pj.id = :id")
    Optional<PassengerJourney> findByIdIncludingDeleted(@Param("id") UUID id);

    // Find by status
    Page<PassengerJourney> findByStatus(JourneyStatus status, Pageable pageable);
    long countByStatus(JourneyStatus status);

    // Find by passenger
    Page<PassengerJourney> findByPassengerId(UUID passengerId, Pageable pageable);
    Page<PassengerJourney> findByPassengerIdAndStatus(UUID passengerId, JourneyStatus status, Pageable pageable);
    Page<PassengerJourney> findByPassengerIdAndStatusIn(UUID passengerId, List<JourneyStatus> statuses, Pageable pageable);
    long countByPassengerId(UUID passengerId);

    // Find by date range
    Page<PassengerJourney> findByMainStopArrivalTimeBetween(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    // Find by pickup or dropoff place
    Page<PassengerJourney> findByPickupPlaceIdOrDropoffPlaceId(UUID pickupPlaceId, UUID dropoffPlaceId, Pageable pageable);
}

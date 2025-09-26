package vn.edu.fpt.transitlink.trip.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.transitlink.trip.entity.Route;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends SoftDeletableRepository<Route, UUID> {

    @Query("""
                SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
                FROM Route r
                WHERE r.driverId = :driverId
                  AND r.isDeleted = false
                  AND r.plannedDepartureTime < :end
                  AND r.plannedArrivalTime > :start
            """)
    boolean existsOverlappingRoute(
            @Param("driverId") UUID driverId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );

    @Query("SELECT r.createdBy FROM Route r WHERE r.id = :id")
    UUID findCreatedByById(@Param("id") UUID id);


    Long countByCreatedByAndIsDeletedFalse(UUID createdUserId);

    Page<Route> findAllByCreatedByAndIsDeletedFalse(UUID createdBy, Pageable pageable);

    @Query("SELECT r FROM Route r WHERE r.driverId = :driverId AND r.status = 'PUBLISHED' AND r.isDeleted = false")
    List<Route> findAllByDriverId(UUID driverId);

    Optional<Route> findByIdAndStatus(UUID id, RouteStatus status);

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Route r WHERE r.isDeleted = true AND r.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

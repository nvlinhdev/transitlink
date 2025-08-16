package vn.edu.fpt.transitlink.route.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.route.entity.Route;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface RouteRepository extends SoftDeletableRepository<Route, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Route r WHERE r.deleted = true AND r.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

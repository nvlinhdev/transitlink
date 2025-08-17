package vn.edu.fpt.transitlink.monitoring.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.monitoring.entity.MonitoringDashboard;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface MonitoringRepository extends SoftDeletableRepository<MonitoringDashboard, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringDashboard md WHERE md.deleted = true AND md.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

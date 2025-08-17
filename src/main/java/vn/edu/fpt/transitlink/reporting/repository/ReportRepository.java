package vn.edu.fpt.transitlink.reporting.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.reporting.entity.Report;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface ReportRepository extends SoftDeletableRepository<Report, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.deleted = true AND r.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

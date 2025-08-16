package vn.edu.fpt.transitlink.schedule.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.schedule.entity.Schedule;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface ScheduleRepository extends SoftDeletableRepository<Schedule, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.deleted = true AND s.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

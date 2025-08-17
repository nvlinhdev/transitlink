package vn.edu.fpt.transitlink.staff_management.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.staff_management.entity.StaffAccount;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface StaffManagementRepository extends SoftDeletableRepository<StaffAccount, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM StaffAccount sa WHERE sa.deleted = true AND sa.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

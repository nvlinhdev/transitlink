package vn.edu.fpt.transitlink.navigation.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.navigation.entity.NavigationData;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface NavigationRepository extends SoftDeletableRepository<NavigationData, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM NavigationData n WHERE n.deleted = true AND n.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);
}

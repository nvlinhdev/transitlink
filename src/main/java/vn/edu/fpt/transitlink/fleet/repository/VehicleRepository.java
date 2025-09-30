package vn.edu.fpt.transitlink.fleet.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;
import vn.edu.fpt.transitlink.fleet.entity.Vehicle;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface VehicleRepository extends SoftDeletableRepository<Vehicle, UUID> {

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Vehicle v WHERE v.isDeleted = true AND v.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);

    // Method to get list of soft-deleted vehicles
    @Query("SELECT v FROM Vehicle v WHERE v.isDeleted = true")
    Page<Vehicle> findAllDeleted(Pageable pageable);

    // Method to count soft-deleted vehicles
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.isDeleted = true")
    long countDeleted();
}

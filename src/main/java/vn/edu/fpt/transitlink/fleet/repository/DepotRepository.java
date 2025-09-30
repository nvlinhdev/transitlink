package vn.edu.fpt.transitlink.fleet.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.transitlink.fleet.entity.Depot;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DepotRepository extends SoftDeletableRepository<Depot, UUID> {
    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Depot d WHERE d.isDeleted = true AND d.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);

    // Các phương thức khác như findById, findAll, count đã được ghi đè trong SoftDeletableRepository
    // và sẽ tự động loại trừ các bản ghi đã bị xóa mềm

    // Phương thức để lấy danh sách các depot đã bị xóa mềm
    @Query("SELECT d FROM Depot d WHERE d.isDeleted = true")
    Page<Depot> findAllDeleted(Pageable pageable);

    // Phương thức để đếm số lượng depot đã bị xóa mềm
    @Query("SELECT COUNT(d) FROM Depot d WHERE d.isDeleted = true")
    long countDeleted();
}

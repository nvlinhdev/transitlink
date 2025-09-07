package vn.edu.fpt.transitlink.shared.base;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface SoftDeletableRepository<T extends BaseSoftDeletableEntity, ID> extends JpaRepository<T, ID> {
    @Modifying
    @Transactional
    int hardDeleteSoftDeletedBefore(@Param("threshold") OffsetDateTime threshold);

    /**
     * Ghi đè phương thức findById để chỉ trả về bản ghi chưa bị xóa mềm
     * @param id ID của entity cần tìm
     * @return Optional chứa entity nếu tìm thấy và chưa bị xóa, ngược lại là Optional.empty()
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.isDeleted = false")
    Optional<T> findById(@Param("id") ID id);

    /**
     * Ghi đè phương thức findAll để chỉ trả về các bản ghi chưa bị xóa mềm
     * @return Danh sách các entity chưa bị xóa
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = false")
    List<T> findAll();

    /**
     * Ghi đè phương thức findAll với Pageable để chỉ trả về các bản ghi chưa bị xóa mềm
     * @param pageable Thông tin phân trang
     * @return Page chứa các entity chưa bị xóa
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = false")
    Page<T> findAll(Pageable pageable);

    /**
     * Ghi đè phương thức count để chỉ đếm các bản ghi chưa bị xóa mềm
     * @return Số lượng entity chưa bị xóa
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isDeleted = false")
    long count();

    /**
     * Phương thức để tìm kiếm entity theo ID bao gồm cả các bản ghi đã bị xóa mềm
     * Chỉ sử dụng cho các trường hợp đặc biệt như khôi phục dữ liệu đã xóa
     * @param id ID của entity cần tìm
     * @return Optional chứa entity nếu tìm thấy, ngược lại là Optional.empty()
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id")
    Optional<T> findByIdIncludingDeleted(@Param("id") ID id);
}

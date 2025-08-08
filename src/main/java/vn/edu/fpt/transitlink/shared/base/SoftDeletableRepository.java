package vn.edu.fpt.transitlink.shared.base;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

@NoRepositoryBean
public interface SoftDeletableRepository<T, ID> extends JpaRepository<T, ID> {
    @Modifying
    @Transactional
    int hardDeleteSoftDeletedBefore(@Param("threshold") OffsetDateTime threshold);
}

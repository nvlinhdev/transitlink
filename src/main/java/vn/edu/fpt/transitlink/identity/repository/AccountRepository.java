package vn.edu.fpt.transitlink.identity.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.shared.base.SoftDeletableRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AccountRepository extends SoftDeletableRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("""
                SELECT DISTINCT a 
                FROM Account a 
                LEFT JOIN a.roles r
                WHERE a.isDeleted = false
                  AND (r IS NULL OR r.name NOT IN :roleNames)
            """)
    Page<Account> findAllExcludingRoles(@Param("roleNames") Set<RoleName> roleNames, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdIncludingDeleted(@Param("id") UUID id);

    @Override
    @Modifying
    @Transactional
    @Query("DELETE FROM Account acc WHERE acc.isDeleted = true AND acc.deletedAt < :threshold")
    int hardDeleteSoftDeletedBefore(OffsetDateTime threshold);

    // Phương thức để lấy danh sách các tài khoản đã bị xóa mềm
    @Query("""
                SELECT DISTINCT a 
                FROM Account a 
                LEFT JOIN a.roles r
                WHERE a.isDeleted = true
                  AND (r IS NULL OR r.name NOT IN :roleNames)
            """)
    Page<Account> findAllDeletedExcludingRoles(@Param("roleNames") Set<RoleName> roleNames, Pageable pageable);

    // Phương thức để đếm số lượng tài khoản đã bị xóa mềm
    @Query("SELECT COUNT(DISTINCT a) FROM Account a LEFT JOIN a.roles r WHERE a.isDeleted = true AND (r IS NULL OR r.name NOT IN :roleNames)")
    long countDeletedExcludingRoles(@Param("roleNames") Set<RoleName> roleNames);

    // Phương thức để kiểm tra xem có tài khoản nào với role cụ thể hay không
    @Query("SELECT COUNT(a) > 0 FROM Account a JOIN a.roles r WHERE a.isDeleted = false AND r.name = :roleName")
    boolean existsByRoleName(@Param("roleName") RoleName roleName);
}

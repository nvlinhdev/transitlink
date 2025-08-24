package vn.edu.fpt.transitlink.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.auth.entity.Role;
import vn.edu.fpt.transitlink.auth.enumeration.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByName(RoleName name);
    Optional<Role> findByName(RoleName name);
}

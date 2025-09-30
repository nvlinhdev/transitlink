package vn.edu.fpt.transitlink.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByName(RoleName name);
    Optional<Role> findByName(RoleName name);
    List<Role> findByNameNot(RoleName excludedRole);

}

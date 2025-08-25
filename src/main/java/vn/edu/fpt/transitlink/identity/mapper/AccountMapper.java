package vn.edu.fpt.transitlink.identity.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.identity.dto.AccountDTO;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO toDTO(Account account);

    // Custom mapping for Role -> RoleName
    default RoleName map(Role role) {
        return role != null ? role.getName() : null;
    }

    // MapStruct sẽ tự động sử dụng khi gặp Set<Role> -> Set<RoleName>
    Set<RoleName> map(Set<Role> roles);
}

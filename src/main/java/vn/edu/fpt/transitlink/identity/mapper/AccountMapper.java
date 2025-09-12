package vn.edu.fpt.transitlink.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.fpt.transitlink.identity.dto.AccountDTO;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.request.ImportAccountRequest;

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

    // Mapping for ImportAccountRequest to Account
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "password", ignore = true) // Will be set manually with encoded password
    @Mapping(target = "emailVerified", constant = "true")
    @Mapping(target = "roles", ignore = true) // Will be set manually after role conversion
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "profileCompleted", ignore = true) // Will be calculated
    @Mapping(target = "avatarUrl", ignore = true)
    Account toEntity(ImportAccountRequest request);
}

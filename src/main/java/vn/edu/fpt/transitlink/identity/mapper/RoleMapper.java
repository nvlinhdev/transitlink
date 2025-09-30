package vn.edu.fpt.transitlink.identity.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.identity.dto.RoleDTO;
import vn.edu.fpt.transitlink.identity.entity.Role;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleDTO roleDTO);
    RoleDTO toDTO(Role role);
    List<RoleDTO> toDTOs(List<Role> roles);
}

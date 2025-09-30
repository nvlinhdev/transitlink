package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.dto.RoleDTO;
import vn.edu.fpt.transitlink.identity.entity.Role;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.mapper.RoleMapper;
import vn.edu.fpt.transitlink.identity.repository.RoleRepository;
import vn.edu.fpt.transitlink.identity.service.RoleService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Cacheable(value = "roles", key = "#name")
    @Override
    public RoleDTO findByName(RoleName name) {
        Role role = roleRepository.findByName(name)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
        return roleMapper.toDTO(role);
    }

    @Cacheable(value = "roles")
    @Override
    public List<RoleDTO> findAll() {
        List<Role> roles = roleRepository.findByNameNot(RoleName.PASSENGER);
        return roleMapper.toDTOs(roles);
    }
}

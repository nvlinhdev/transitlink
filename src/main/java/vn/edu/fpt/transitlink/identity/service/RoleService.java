package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.RoleDTO;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;

import java.util.List;

public interface RoleService {
    RoleDTO findByName(RoleName name);
    List<RoleDTO> findAll();
}

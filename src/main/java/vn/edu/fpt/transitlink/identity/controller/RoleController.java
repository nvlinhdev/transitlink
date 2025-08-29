package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.identity.dto.RoleDTO;
import vn.edu.fpt.transitlink.identity.service.RoleService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

import java.util.List;

@RestController
@RequestMapping("/api/identity/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for fetching roles")
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Get all roles",
            description = "Fetch a list of all available roles"
    )
    @GetMapping
    public ResponseEntity<StandardResponse<List<RoleDTO>>> getAllRoles() {
        List<RoleDTO> roles = roleService.findAll();
        return ResponseEntity.ok(StandardResponse.success(roles));
    }
}


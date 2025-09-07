package vn.edu.fpt.transitlink.fleet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
import vn.edu.fpt.transitlink.fleet.request.CreateDepotRequest;
import vn.edu.fpt.transitlink.fleet.request.UpdateDepotRequest;
import vn.edu.fpt.transitlink.fleet.service.DepotService;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/fleet/depots")
@Tag(name = "Depot Management", description = "APIs for managing depots")
public class DepotController {
    private final DepotService depotService;

    @Operation(summary = "Create depot",
            description = "Create a new depot with location information"
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<DepotDTO>> createDepot(@Valid @RequestBody CreateDepotRequest request) {
        DepotDTO depotDTO = depotService.createDepot(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.created(depotDTO));
    }

    @Operation(summary = "Update depot",
            description = "Update an existing depot's information"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<DepotDTO>> updateDepot(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDepotRequest request) {
        DepotDTO depotDTO = depotService.updateDepot(id, request);
        return ResponseEntity.ok(StandardResponse.success("Depot updated successfully", depotDTO));
    }

    @Operation(summary = "Delete depot",
            description = "Soft delete a depot (mark as deleted)"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<DepotDTO>> softDeleteDepot(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable UUID id) {
        DepotDTO depotDTO = depotService.deleteDepot(id, principal.getId());
        return ResponseEntity.ok(StandardResponse.success("Depot deleted successfully", depotDTO));
    }

    @Operation(summary = "Restore deleted depot",
            description = "Restore a previously deleted depot"
    )
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<DepotDTO>> restoreDepot(@PathVariable UUID id) {
        DepotDTO depotDTO = depotService.restoreDepot(id);
        return ResponseEntity.ok(StandardResponse.success("Depot restored successfully", depotDTO));
    }

    @Operation(summary = "Permanently delete depot",
            description = "Permanently delete a depot (cannot be restored). DEPRECATED: This endpoint is deprecated and will be removed in future versions. Use soft delete instead."
    )
    @Deprecated(since = "1.1.0", forRemoval = true)
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<Void>> hardDeleteDepot(@PathVariable UUID id) {
        depotService.hardDeleteDepot(id);
        return ResponseEntity.ok(StandardResponse.success("Depot permanently deleted successfully, but note that this endpoint is deprecated and will be removed in future versions.", null));
    }

    @Operation(summary = "Get depot by ID",
            description = "Get depot details by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<DepotDTO>> getDepot(@PathVariable UUID id) {
        DepotDTO depotDTO = depotService.getDepot(id);
        return ResponseEntity.ok(StandardResponse.success(depotDTO));
    }

    @Operation(summary = "Get depots (paginated)",
            description = "Get paginated list of depots"
    )
    @GetMapping
    public ResponseEntity<PaginatedResponse<DepotDTO>> getDepots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<DepotDTO> depots = depotService.getDepots(page, size);
        long totalItems = depotService.countDepots();
        PaginatedResponse<DepotDTO> response = new PaginatedResponse<>(depots, page, size, totalItems);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get deleted depots (paginated)",
            description = "Get paginated list of soft-deleted depots"
    )
    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<PaginatedResponse<DepotDTO>> getDeletedDepots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<DepotDTO> deletedDepots = depotService.getDeletedDepots(page, size);
        long totalItems = depotService.countDeletedDepots();
        PaginatedResponse<DepotDTO> response = new PaginatedResponse<>(deletedDepots, page, size, totalItems);
        return ResponseEntity.ok(response);
    }
}

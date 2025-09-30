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
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.request.CreateVehicleRequest;
import vn.edu.fpt.transitlink.fleet.request.UpdateVehicleRequest;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fleet/vehicles")
@Tag(name = "Vehicle Management", description = "APIs for managing vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    @Operation(summary = "Create vehicle",
            description = "Create a new vehicle with depot assignment"
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<VehicleDTO>> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        VehicleDTO vehicleDTO = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.created(vehicleDTO));
    }

    @Operation(summary = "Update vehicle",
            description = "Update an existing vehicle's information"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<VehicleDTO>> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        VehicleDTO vehicleDTO = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(StandardResponse.success("Vehicle updated successfully", vehicleDTO));
    }

    @Operation(summary = "Delete vehicle",
            description = "Soft delete a vehicle (mark as deleted)"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<VehicleDTO>> softDeleteVehicle(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable UUID id) {
        VehicleDTO vehicleDTO = vehicleService.deleteVehicle(id, principal.getId());
        return ResponseEntity.ok(StandardResponse.success("Vehicle deleted successfully", vehicleDTO));
    }

    @Operation(summary = "Restore deleted vehicle",
            description = "Restore a previously deleted vehicle"
    )
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<VehicleDTO>> restoreVehicle(@PathVariable UUID id) {
        VehicleDTO vehicleDTO = vehicleService.restoreVehicle(id);
        return ResponseEntity.ok(StandardResponse.success("Vehicle restored successfully", vehicleDTO));
    }

    @Operation(summary = "Permanently delete vehicle",
            description = "Permanently delete a vehicle (cannot be restored). DEPRECATED: This endpoint is deprecated and will be removed in future versions. Use soft delete instead."
    )
    @Deprecated(since = "1.1.0", forRemoval = true)
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<Void>> hardDeleteVehicle(@PathVariable UUID id) {
        vehicleService.hardDeleteVehicle(id);
        return ResponseEntity.ok(StandardResponse.success("Vehicle permanently deleted successfully, but note that this endpoint is deprecated and will be removed in future versions.", null));
    }

    @Operation(summary = "Get vehicle by ID",
            description = "Get vehicle details by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<VehicleDTO>> getVehicle(@PathVariable UUID id) {
        VehicleDTO vehicleDTO = vehicleService.getVehicle(id);
        return ResponseEntity.ok(StandardResponse.success(vehicleDTO));
    }

    @Operation(summary = "Get vehicles (paginated)",
            description = "Get paginated list of vehicles"
    )
    @GetMapping
    public ResponseEntity<PaginatedResponse<VehicleDTO>> getVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<VehicleDTO> vehicles = vehicleService.getVehicles(page, size);
        long totalItems = vehicleService.countVehicles();
        PaginatedResponse<VehicleDTO> response = new PaginatedResponse<>(vehicles, page, size, totalItems);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get deleted vehicles (paginated)",
            description = "Get paginated list of soft-deleted vehicles"
    )
    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<PaginatedResponse<VehicleDTO>> getDeletedVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<VehicleDTO> deletedVehicles = vehicleService.getDeletedVehicles(page, size);
        long totalItems = vehicleService.countDeletedVehicles();
        PaginatedResponse<VehicleDTO> response = new PaginatedResponse<>(deletedVehicles, page, size, totalItems);
        return ResponseEntity.ok(response);
    }
}

package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.dto.DriverDTO;
import vn.edu.fpt.transitlink.identity.request.CreateDriverRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateDriverRequest;
import vn.edu.fpt.transitlink.identity.service.DriverService;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/identity/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Management", description = "APIs for managing driver information")
public class DriverController {

    private final DriverService driverService;

    @Operation(summary = "Create driver",
            description = "Create a new driver with associated account")
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping
    public ResponseEntity<StandardResponse<DriverDTO>> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        DriverDTO result = driverService.createDriver(request);
        return ResponseEntity.status(201).body(StandardResponse.created(result));
    }

    @Operation(summary = "Get driver by ID",
            description = "Get driver details by ID (accessible to all authenticated users)")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<DriverDTO>> getDriverById(@PathVariable UUID id) {
        DriverDTO result = driverService.getDriverById(id);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Update driver",
            description = "Update driver details (Manager only)")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<DriverDTO>> updateDriver(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDriverRequest request) {
        DriverDTO result = driverService.updateDriver(id, request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Delete driver",
            description = "Soft delete driver (Manager only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<DriverDTO>> deleteDriver(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable UUID id) {
        DriverDTO deletedDriver = driverService.deleteDriver(id, principal.getId());
        return ResponseEntity.ok(
                StandardResponse.success("Driver deleted successfully", deletedDriver)
        );
    }

    @Operation(summary = "Restore driver",
            description = "Restore a soft-deleted driver (Manager only)")
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StandardResponse<DriverDTO>> restoreDriver(@PathVariable UUID id) {
        DriverDTO restoredDriver = driverService.restoreDriver(id);
        return ResponseEntity.ok(
                StandardResponse.success("Driver restored successfully", restoredDriver)
        );
    }

    @Operation(summary = "Get drivers (paginated)",
            description = "Get paginated list of drivers (Manager and Dispatcher only)")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<DriverDTO>> getDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<DriverDTO> drivers = driverService.getDrivers(page, size);
        long total = driverService.countDrivers();
        PaginatedResponse<DriverDTO> response = new PaginatedResponse<>(drivers, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get deleted drivers (paginated)",
            description = "Get paginated list of soft-deleted drivers (Manager only)")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/deleted")
    public ResponseEntity<PaginatedResponse<DriverDTO>> getDeletedDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<DriverDTO> deletedDrivers = driverService.getDeletedDrivers(page, size);
        long total = driverService.countDeletedDrivers();
        PaginatedResponse<DriverDTO> response = new PaginatedResponse<>(deletedDrivers, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's driver profile",
            description = "Get driver details for the currently authenticated user (if they are a driver)")
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<DriverDTO>> getCurrentUserDriverProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        DriverDTO result = driverService.getCurrentDriverByAccountId(principal.getId());
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Update current user's driver profile",
            description = "Update driver details for the currently authenticated user (if they are a driver)")
    @PutMapping("/me")
    public ResponseEntity<StandardResponse<DriverDTO>> updateCurrentUserDriverProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdateDriverRequest request) {
        DriverDTO result = driverService.updateCurrentDriver(principal.getId(), request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }
}

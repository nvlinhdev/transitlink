package vn.edu.fpt.transitlink.identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.service.PassengerService;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/identity/passengers")
@RequiredArgsConstructor
@Tag(name = "Passenger Management", description = "APIs for managing passenger information")
public class PassengerController {

    private final PassengerService passengerService;

    @Operation(summary = "Get passenger by ID",
            description = "Get passenger details by ID (accessible to all authenticated users)")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<PassengerDTO>> getPassengerById(@PathVariable UUID id) {
        PassengerDTO result = passengerService.getPassengerById(id);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Get passengers (paginated)",
            description = "Get paginated list of passengers (Manager and Dispatcher only)")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<PassengerDTO>> getPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerDTO> passengers = passengerService.getPassengers(page, size);
        long total = passengerService.countPassengers();
        PaginatedResponse<PassengerDTO> response = new PaginatedResponse<>(passengers, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get deleted passengers (paginated)",
            description = "Get paginated list of soft-deleted passengers (Manager and Dispatcher only)")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    @GetMapping("/deleted")
    public ResponseEntity<PaginatedResponse<PassengerDTO>> getDeletedPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerDTO> deletedPassengers = passengerService.getDeletedPassengers(page, size);
        long total = passengerService.countDeletedPassengers();
        PaginatedResponse<PassengerDTO> response = new PaginatedResponse<>(deletedPassengers, page, size, total);
        return ResponseEntity.ok(response);
    }

    // ================ /ME ENDPOINTS FOR PASSENGERS ================

    @Operation(summary = "Get current user's passenger profile",
            description = "Get passenger details for the currently authenticated user (if they are a passenger)")
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<PassengerDTO>> getCurrentUserPassengerProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        PassengerDTO result = passengerService.getCurrentPassengerByAccountId(principal.getId());
        return ResponseEntity.ok(StandardResponse.success(result));
    }
}

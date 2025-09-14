package vn.edu.fpt.transitlink.trip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;
import vn.edu.fpt.transitlink.trip.dto.ImportJourneyResultDTO;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.request.CreatePassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.ImportPassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.SearchPassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.UpdatePassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.service.PassengerJourneyService;

import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trip/passenger-journeys")
@Tag(name = "Passenger Journey Management", description = "APIs for managing passenger journey operations")
public class PassengerJourneyController {
    private final PassengerJourneyService passengerJourneyService;

    // ==================== CRUD OPERATIONS ====================

    @Operation(summary = "Create new passenger journey",
            description = "Create a new passenger journey request (TICKET_SELLER only)")
    @PostMapping
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<StandardResponse<PassengerJourneyDTO>> createPassengerJourney(
            @Valid @RequestBody CreatePassengerJourneyRequest request) {
        PassengerJourneyDTO result = passengerJourneyService.createPassengerJourney(request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Get passenger journey by ID",
            description = "Get passenger journey details by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PASSENGER', 'TICKET_SELLER', 'DISPATCHER')")
    public ResponseEntity<StandardResponse<PassengerJourneyDTO>> getPassengerJourneyById(@PathVariable UUID id) {
        PassengerJourneyDTO result = passengerJourneyService.getPassengerJourneyById(id);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Update passenger journey",
            description = "Update passenger journey details (TICKET_SELLER only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<StandardResponse<PassengerJourneyDTO>> updatePassengerJourney(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePassengerJourneyRequest request) {
        PassengerJourneyDTO result = passengerJourneyService.updatePassengerJourney(id, request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Delete passenger journey",
            description = "Soft delete passenger journey (TICKET_SELLER only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<StandardResponse<PassengerJourneyDTO>> deletePassengerJourney(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        PassengerJourneyDTO result = passengerJourneyService.deletePassengerJourney(id, principal.getId());
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    @Operation(summary = "Restore passenger journey",
            description = "Restore soft-deleted passenger journey (TICKET_SELLER only)")
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<StandardResponse<PassengerJourneyDTO>> restorePassengerJourney(@PathVariable UUID id) {
        PassengerJourneyDTO result = passengerJourneyService.restorePassengerJourney(id);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

    // ==================== LIST OPERATIONS (TICKET_SELLER) ====================

    @Operation(summary = "Get all passenger journeys (paginated)",
            description = "Get paginated list of all passenger journeys (TICKET_SELLER only)")
    @GetMapping
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getPassengerJourneys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> journeys = passengerJourneyService.getPassengerJourneys(page, size);
        long total = passengerJourneyService.countPassengerJourneys();
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(journeys, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get deleted passenger journeys (paginated)",
            description = "Get paginated list of soft-deleted passenger journeys (TICKET_SELLER only)")
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getDeletedPassengerJourneys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> deletedJourneys = passengerJourneyService.getDeletedPassengerJourneys(page, size);
        long total = passengerJourneyService.countDeletedPassengerJourneys();
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(deletedJourneys, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get unscheduled journeys",
            description = "Get journeys that haven't been scheduled yet (TICKET_SELLER only)")
    @GetMapping("/unscheduled")
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getUnscheduledJourneys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> unscheduledJourneys = passengerJourneyService.getUnscheduledJourneys(page, size);
        long total = passengerJourneyService.countJourneysByStatus(JourneyStatus.NOT_SCHEDULED);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(unscheduledJourneys, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get journeys by status",
            description = "Get journeys filtered by status (TICKET_SELLER and DISPATCHER)")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('TICKET_SELLER', 'DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getJourneysByStatus(
            @PathVariable JourneyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> journeys = passengerJourneyService.getPassengerJourneysByStatus(status, page, size);
        long total = passengerJourneyService.countJourneysByStatus(status);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(journeys, page, size, total);
        return ResponseEntity.ok(response);
    }

    // ==================== DISPATCHER OPERATIONS ====================

    @Operation(summary = "Get journeys for today",
            description = "Get all journeys scheduled for today (DISPATCHER only)")
    @GetMapping("/today")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getJourneysForToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> todayJourneys = passengerJourneyService.getJourneysForToday(page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(todayJourneys, page, size, todayJourneys.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get journeys for a specific week",
            description = "Get all journeys for a specific week (DISPATCHER only)")
    @GetMapping("/week")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getJourneysForWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime weekStart,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> weekJourneys = passengerJourneyService.getJourneysForWeek(weekStart, page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(weekJourneys, page, size, weekJourneys.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get journeys for a specific month",
            description = "Get all journeys for a specific month (DISPATCHER only)")
    @GetMapping("/month")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getJourneysForMonth(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> monthJourneys = passengerJourneyService.getJourneysForMonth(year, month, page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(monthJourneys, page, size, monthJourneys.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get journeys by date range",
            description = "Get journeys within a specific date range (DISPATCHER only)")
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getJourneysByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> rangeJourneys = passengerJourneyService.getJourneysByDateRange(startDate, endDate, page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(rangeJourneys, page, size, rangeJourneys.size());
        return ResponseEntity.ok(response);
    }

    // ==================== PASSENGER OPERATIONS ====================

    @Operation(summary = "Get current user's journeys",
            description = "Get current passenger's active journeys (PASSENGER only)")
    @GetMapping("/me")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getCurrentPassengerJourneys(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> myJourneys = passengerJourneyService.getCurrentPassengerJourneys(principal.getId(), page, size);
        long total = passengerJourneyService.countJourneysByPassenger(principal.getId());
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(myJourneys, page, size, total);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's completed journeys",
            description = "Get current passenger's completed journeys (PASSENGER only)")
    @GetMapping("/me/completed")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getMyCompletedJourneys(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> completedJourneys = passengerJourneyService.getPassengerCompletedJourneys(principal.getId(), page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(completedJourneys, page, size, completedJourneys.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's cancelled journeys",
            description = "Get current passenger's cancelled journeys (PASSENGER only)")
    @GetMapping("/me/cancelled")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getMyCancelledJourneys(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> cancelledJourneys = passengerJourneyService.getPassengerCancelledJourneys(principal.getId(), page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(cancelledJourneys, page, size, cancelledJourneys.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's journey history",
            description = "Get current passenger's complete journey history (PASSENGER only)")
    @GetMapping("/me/history")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> getMyJourneyHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> historyJourneys = passengerJourneyService.getPassengerJourneyHistory(principal.getId(), page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(historyJourneys, page, size, historyJourneys.size());
        return ResponseEntity.ok(response);
    }

    // ==================== SEARCH OPERATIONS ====================

    @Operation(summary = "Search passenger journeys",
            description = "Advanced search for passenger journeys (TICKET_SELLER and DISPATCHER only)")
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('TICKET_SELLER', 'DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> searchPassengerJourneys(
            @Valid @RequestBody SearchPassengerJourneyRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> searchResults = passengerJourneyService.searchPassengerJourneys(request, page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(searchResults, page, size, searchResults.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search by passenger name or email",
            description = "Search journeys by passenger name or email (TICKET_SELLER and DISPATCHER only)")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('TICKET_SELLER', 'DISPATCHER')")
    public ResponseEntity<PaginatedResponse<PassengerJourneyDTO>> searchByPassengerNameOrEmail(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PassengerJourneyDTO> searchResults = passengerJourneyService.searchByPassengerNameOrEmail(query, page, size);
        PaginatedResponse<PassengerJourneyDTO> response = new PaginatedResponse<>(searchResults, page, size, searchResults.size());
        return ResponseEntity.ok(response);
    }

    // ==================== BULK OPERATIONS ====================

    @Operation(summary = "Import passenger journeys from Excel file",
            description = "Bulk import passenger journeys from Excel file (TICKET_SELLER only)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('TICKET_SELLER')")
    public ResponseEntity<StandardResponse<ImportJourneyResultDTO>> importPassengerJourneysFromExcel(
            @Parameter(description = "Mapping configuration (JSON)")
            @RequestPart("request") ImportPassengerJourneyRequest request,

            @Parameter(description = "Excel file to import")
            @RequestPart("file") MultipartFile file
    ) {
        ImportJourneyResultDTO result = passengerJourneyService.importPassengerJourneysFromExcel(file, request);
        return ResponseEntity.ok(StandardResponse.success(result));
    }

}

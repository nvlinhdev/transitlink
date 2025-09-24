package vn.edu.fpt.transitlink.trip.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.shared.dto.PaginatedResponse;
import vn.edu.fpt.transitlink.shared.security.CustomUserPrincipal;
import vn.edu.fpt.transitlink.trip.dto.*;
import vn.edu.fpt.transitlink.trip.request.AssignDriverRequest;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;
import vn.edu.fpt.transitlink.trip.service.RouteService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trip/routes")
@Tag(name = "Route Controller")
public class RouteController {
    private final RouteService routeService;

    @PreAuthorize("hasRole('DISPATCHER')")
    @PostMapping
    public ResponseEntity<StandardResponse<OptimizationResultDTO>> optimizeRoute(@RequestBody OptimizationRouteRequest request) {
        OptimizationResultDTO resultDTO = routeService.optimizeRoute(request);
        return  ResponseEntity.ok(StandardResponse.success(resultDTO));
    }

    @PreAuthorize("hasRole('DISPATCHER')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<RouteSummaryDTO>> getRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        List<RouteSummaryDTO> routes = routeService.getRoutes(page, size, principal.getId());
        long totalRoutes = routeService.countRoutes(principal.getId());
        PaginatedResponse<RouteSummaryDTO> response = new PaginatedResponse<>(routes, page, size, totalRoutes);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DISPATCHER')")
    @GetMapping("/{routeId}")
    public ResponseEntity<StandardResponse<RouteDetailDTO>> getRoute(@PathVariable @NotNull UUID routeId) {
        RouteDetailDTO routeDetail = routeService.getRoute(routeId);
        return ResponseEntity.ok(StandardResponse.success(routeDetail));
    }

    @PreAuthorize("hasRole('DISPATCHER')")
    @PostMapping("/assign-driver")
    public ResponseEntity<StandardResponse<Void>> assignDriverToRoute(@Valid @RequestBody AssignDriverRequest request) {
        routeService.assignDriverToRoute(request.driverId(), request.routeId());
        return ResponseEntity.ok(StandardResponse.success("Driver assigned to route successfully", null));
    }

    @PreAuthorize("hasRole('DISPATCHER')")
    @PostMapping("/publish/{routeId}")
    public ResponseEntity<StandardResponse<Void>> publishRoute(@PathVariable UUID routeId) {
        routeService.publishRoute(routeId);
        return ResponseEntity.ok(StandardResponse.success("Route published successfully", null));
    }

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/driver-routes")
    public ResponseEntity<List<DriverRouteSummaryDTO>> getAllDriverRouteByDriverId(UUID driverId) {
        List<DriverRouteSummaryDTO> driverRouteSummaries = routeService.getAllDriverRouteByDriverId(driverId);
        return ResponseEntity.ok(driverRouteSummaries);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/driver-routes/{routeId}")
    public ResponseEntity<DriverRouteDetailDTO> getDriverRouteById(@PathVariable UUID routeId) {
        DriverRouteDetailDTO driverRouteDetail = routeService.getDriverRouteById(routeId);
        return ResponseEntity.ok(driverRouteDetail);
    }
}

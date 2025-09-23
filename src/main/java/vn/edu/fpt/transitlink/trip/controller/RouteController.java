package vn.edu.fpt.transitlink.trip.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;
import vn.edu.fpt.transitlink.trip.service.RouteService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.trip.dto.RouteDTO;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trip/routes")
@Tag(name = "Route Controller")
public class RouteController {
    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<StandardResponse<List<RouteDTO>>> optimizeRoute(@RequestBody OptimizationRouteRequest request) {
        List<RouteDTO> routeDTO = routeService.optimizeRoute(request);
        return  ResponseEntity.ok(StandardResponse.success(routeDTO));
    }

}

package vn.edu.fpt.transitlink.trip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.trip.service.RouteService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.trip.dto.RouteDTO;

@RequestMapping("/api/trip/routes")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<RouteDTO>> createRouteData(
            // @Valid @RequestBody CreateRouteRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{id}/override")
    public ResponseEntity<StandardResponse<Void>> overrideRoute() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> viewRouteList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteRouteData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

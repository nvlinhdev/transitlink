package vn.edu.fpt.transitlink.navigation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.navigation.service.NavigationService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RestController("/api/navigation")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    // View Navigation & ETA
    @GetMapping("/{tripId}")
    public ResponseEntity<StandardResponse<Void>> viewNavigation() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/navigation/{driverId}/tracking → View Driver Tracking & ETA
    @GetMapping("/{driverId}/tracking")
    public ResponseEntity<StandardResponse<Void>> viewDriverTracking() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}

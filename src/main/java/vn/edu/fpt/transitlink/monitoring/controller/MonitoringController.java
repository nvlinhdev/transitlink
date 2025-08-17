package vn.edu.fpt.transitlink.monitoring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.monitoring.service.MonitoringService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;


@RestController("/api/schedules")
public class MonitoringController {
    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping()
    public ResponseEntity<StandardResponse<Void>> viewMonitoring() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}

package vn.edu.fpt.transitlink.reporting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.reporting.service.ReportService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RestController("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    //GET /api/reports/trips → Generate Trip Report
    @GetMapping("/trips")
    public ResponseEntity<StandardResponse<Void>> generateTripReport() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/passengers → Generate Passenger Report
    @GetMapping("/passengers")
    public ResponseEntity<StandardResponse<Void>> generatePassengerReport() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/drivers → Generate Driver Report
    @GetMapping("/drivers")
    public ResponseEntity<StandardResponse<Void>> generateDriverReport() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/vehicles → Generate Vehicle Usage Report
    @GetMapping("/vehicles")
    public ResponseEntity<StandardResponse<Void>> generateVehicleUsageReport() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/incidents → Generate Incident Report
    @GetMapping("/incidents")
    public ResponseEntity<StandardResponse<Void>> generateIncidentReport() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/export → Export Report
    @GetMapping("/export")
    public ResponseEntity<StandardResponse<Void>> exportReport() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/dashboard → View Reports Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<StandardResponse<Void>> viewReportsDashboard() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/reports/filter → Filter Report Data
    @GetMapping("/filter")
    public ResponseEntity<StandardResponse<Void>> filterReportData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }




}

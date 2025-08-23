package vn.edu.fpt.transitlink.fleet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RestController
@RequestMapping("/api/fleet/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<VehicleDTO>> enterVehicleData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/import")
    public ResponseEntity<StandardResponse<Void>> importVehicleData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<StandardResponse<Void>> viewVehicleList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteVehicleData(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

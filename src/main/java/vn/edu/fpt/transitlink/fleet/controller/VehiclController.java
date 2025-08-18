package vn.edu.fpt.transitlink.fleet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RestController("/api/fleet/vehicles")
public class VehiclController {

    private final VehicleService vehicleService;

    public VehiclController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }


    @PostMapping
    public ResponseEntity<StandardResponse<VehicleDTO>> enterVehicleData(
            // @Valid @RequestBody EnterVehicleDataRequest request,
            // Principal principal
    ) {
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
    public ResponseEntity<StandardResponse<Void>> deleteVehicleData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

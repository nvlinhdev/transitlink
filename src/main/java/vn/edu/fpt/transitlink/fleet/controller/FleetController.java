package vn.edu.fpt.transitlink.fleet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.fleet.service.FleetService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;

@RestController("/api/fleet")
public class FleetController {

    private final FleetService fleetService;

    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    //Depots


    //Vehicle
    @PostMapping("/vehicles")
    public ResponseEntity<StandardResponse<VehicleDTO>> enterVehicleData(
            // @Valid @RequestBody EnterVehicleDataRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/vehicles/import")
    public ResponseEntity<StandardResponse<Void>> importVehicleData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/vehicles")
    public ResponseEntity<StandardResponse<Void>> viewVehicleList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteVehicleData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

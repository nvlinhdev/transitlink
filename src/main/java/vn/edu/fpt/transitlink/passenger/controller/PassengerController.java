package vn.edu.fpt.transitlink.passenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;
import vn.edu.fpt.transitlink.passenger.service.PassengerService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

import java.security.Principal;

@RestController("/api/passengers")

public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<PassengerDTO>> enterPassengerData(
        // @Valid @RequestBody EnterPassengerDataRequest request,
        // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/import")
    public ResponseEntity<StandardResponse<Void>> importPassengerData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<StandardResponse<Void>> viewPassengerList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deletePassengerData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}

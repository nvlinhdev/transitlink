package vn.edu.fpt.transitlink.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.user.dto.PassengerDTO;
import vn.edu.fpt.transitlink.user.service.PassengerService;

@RequestMapping("/api/user/passengers")
public class PassengerController {
    private final PassengerService service;

    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @PostMapping("/passengers")
    public ResponseEntity<StandardResponse<PassengerDTO>> enterPassengerData(
            // @Valid @RequestBody EnterPassengerDataRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/passengers/import")
    public ResponseEntity<StandardResponse<Void>> importPassengerData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/passengers")
    public ResponseEntity<StandardResponse<Void>> viewPassengerList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/passengers/{id}")
    public ResponseEntity<StandardResponse<Void>> deletePassengerData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


}

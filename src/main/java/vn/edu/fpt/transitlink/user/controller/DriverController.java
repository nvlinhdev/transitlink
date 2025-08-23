package vn.edu.fpt.transitlink.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.user.dto.DriverDTO;
import vn.edu.fpt.transitlink.user.service.DriverService;

@RequestMapping("/api/user/drivers")
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<StandardResponse<DriverDTO>> enterDriverData(
            // @Valid @RequestBody EnterDriverDataRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/import")
    public ResponseEntity<StandardResponse<Void>> importDriverData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<StandardResponse<Void>> viewDriverList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteDriverData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

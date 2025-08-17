package vn.edu.fpt.transitlink.staff_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.schedule.dto.ScheduleDTO;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.staff_management.service.StaffManagementService;

@RestController("/api/staff")
public class StaffManagementController {

    private final StaffManagementService staffManagementService;

    public StaffManagementController(StaffManagementService staffManagementService) {
        this.staffManagementService = staffManagementService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<ScheduleDTO>> createStaffAccount(
            // @Valid @RequestBody CreateStaffAccountRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteStaffAccount() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

package vn.edu.fpt.transitlink.schedule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.schedule.dto.ScheduleDTO;
import vn.edu.fpt.transitlink.schedule.service.ScheduleService;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

@RestController("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse<ScheduleDTO>> createScheduleData(
            // @Valid @RequestBody CreateScheduleDataRequest request,
            // Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{id}/override")
    public ResponseEntity<StandardResponse<Void>> overrideSchedule() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> viewScheduleList() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteScheduleData() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}

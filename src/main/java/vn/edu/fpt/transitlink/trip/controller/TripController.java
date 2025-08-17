package vn.edu.fpt.transitlink.trip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;
import vn.edu.fpt.transitlink.trip.service.TripService;

@RestController("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    //GET /api/trips/summary → View Trips Summary
    @GetMapping("/summary")
    public ResponseEntity<StandardResponse<Void>> viewTripsSummary() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/trips/{id} → View Trip Info
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> viewTripInfo() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/trips/history/driver/{driverId} → View Trip History For Driver
    @GetMapping("/history/driver/{driverId}")
    public ResponseEntity<StandardResponse<Void>> viewTripHistoryForDriver() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/trips/history/passenger/{passengerId} → View Trip History For Passenger
    @GetMapping("/history/passenger/{passengerId}")
    public ResponseEntity<StandardResponse<Void>> viewTripHistoryForPassenger() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //GET /api/trips/history/dispatcher/{dispatcherId} → View Trip History For Dispatcher
    @GetMapping("/history/dispatcher/{dispatcherId}")
    public ResponseEntity<StandardResponse<Void>> viewTripHistoryForDispatcher() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //POST /api/trips/{id}/checkin → Check-in
    @GetMapping("/{id}/checkin")
    public ResponseEntity<StandardResponse<Void>> checkIn() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //POST /api/trips/{id}/checkout → Check-out
    @GetMapping("/{id}/checkout")
    public ResponseEntity<StandardResponse<Void>> checkOut() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //POST /api/trips/{id}/pickup/confirm → Confirm Pick-up
    @GetMapping("/{id}/pickup/confirm")
    public ResponseEntity<StandardResponse<Void>> confirmPickUp() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    //POST /api/trips/{id}/dropoff/confirm → Confirm Drop-off
    @GetMapping("/{id}/dropoff/confirm")
    public ResponseEntity<StandardResponse<Void>> confirmDropOff() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}

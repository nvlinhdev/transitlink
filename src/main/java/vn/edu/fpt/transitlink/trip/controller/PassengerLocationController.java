package vn.edu.fpt.transitlink.trip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.trip.service.PassengerLocationService;

@RequestMapping("/api/trip/passenger-location")
public class PassengerLocationController {
    private final PassengerLocationService passengerLocationService;

    public PassengerLocationController(PassengerLocationService passengerLocationService) {
        this.passengerLocationService = passengerLocationService;
    }

}

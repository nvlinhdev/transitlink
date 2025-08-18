package vn.edu.fpt.transitlink.location.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.location.service.PassengerLocationService;

@RequestMapping("/api/location/passenger-location")
public class PassengerLocationController {
    private final PassengerLocationService passengerLocationService;

    public PassengerLocationController(PassengerLocationService passengerLocationService) {
        this.passengerLocationService = passengerLocationService;
    }

}

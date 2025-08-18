package vn.edu.fpt.transitlink.trip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.trip.service.DriverLocationService;

@RequestMapping("/api/trip/driver-location")
public class DriverLocationController {
    private final DriverLocationService driverLocationService;

    public DriverLocationController(DriverLocationService driverLocationService) {
        this.driverLocationService = driverLocationService;
    }

}

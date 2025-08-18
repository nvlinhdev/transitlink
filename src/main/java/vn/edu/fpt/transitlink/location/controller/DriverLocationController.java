package vn.edu.fpt.transitlink.location.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.location.service.DriverLocationService;

@RequestMapping("/api/location/driver-location")
public class DriverLocationController {
    private final DriverLocationService driverLocationService;

    public DriverLocationController(DriverLocationService driverLocationService) {
        this.driverLocationService = driverLocationService;
    }

}

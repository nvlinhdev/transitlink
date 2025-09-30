package vn.edu.fpt.transitlink.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import vn.edu.fpt.transitlink.location.dto.DriverLocationMessage;
import vn.edu.fpt.transitlink.location.dto.PassengerLocationMessage;
import vn.edu.fpt.transitlink.location.service.LocationTrackingService;

@RequiredArgsConstructor
@Controller
public class LocationTrackingController {
    private final LocationTrackingService locationService;

    @MessageMapping("/driver/location")
    public void receiveDriverLocation(DriverLocationMessage msg) {
        locationService.updateDriverLocation(msg);
    }

    @MessageMapping("/passenger/location")
    public void receivePassengerLocation(PassengerLocationMessage msg) {
        locationService.updatePassengerLocation(msg);
    }
}

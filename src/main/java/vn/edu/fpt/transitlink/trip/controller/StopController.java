package vn.edu.fpt.transitlink.trip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.trip.service.StopService;

@RequestMapping("/api/trip/stops")
public class StopController {

    private final StopService stopService;

    public StopController(StopService stopService) {
        this.stopService = stopService;
    }

}

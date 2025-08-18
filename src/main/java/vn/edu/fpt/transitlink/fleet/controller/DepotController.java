package vn.edu.fpt.transitlink.fleet.controller;

import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.transitlink.fleet.service.DepotService;

@RestController("/api/fleet/depot")
public class DepotController {

    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }
}

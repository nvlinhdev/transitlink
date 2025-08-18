package vn.edu.fpt.transitlink.location.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.location.service.PlaceService;

@RequestMapping("/api/location/place")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }
}

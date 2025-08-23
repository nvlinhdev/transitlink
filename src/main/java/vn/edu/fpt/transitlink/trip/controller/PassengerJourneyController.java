package vn.edu.fpt.transitlink.trip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.transitlink.trip.service.PassengerJourneyService;

@RequestMapping("/api/trip/passenger-journey")
public class PassengerJourneyController {

    private final PassengerJourneyService passengerJourneyService;

    public PassengerJourneyController(PassengerJourneyService passengerJourneyService) {
        this.passengerJourneyService = passengerJourneyService;
    }
}

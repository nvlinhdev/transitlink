package vn.edu.fpt.transitlink.trip.dto;

import vn.edu.fpt.transitlink.trip.enumeration.StopAction;

public record PassengerOnStopDTO(
        PassengerJourneyInfo passengerJourneyInfo,
        StopAction action
) {
}

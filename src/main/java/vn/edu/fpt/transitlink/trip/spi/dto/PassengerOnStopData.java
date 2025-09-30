package vn.edu.fpt.transitlink.trip.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.edu.fpt.transitlink.trip.enumeration.StopAction;

import java.util.UUID;

@AllArgsConstructor
@Data
public class PassengerOnStopData {
    UUID passengerJourneyId;
    StopAction action;
}

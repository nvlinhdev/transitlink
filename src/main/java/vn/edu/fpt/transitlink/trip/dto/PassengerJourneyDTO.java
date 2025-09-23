package vn.edu.fpt.transitlink.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassengerJourneyDTO {
    private UUID id;
    private PassengerDTO passenger;
    private PlaceDTO pickupPlace;
    private PlaceDTO dropoffPlace;
    private OffsetDateTime mainStopArrivalTime;
    private Integer seatCount;
    private JourneyType journeyType;
    private String geometry;
    private OffsetDateTime plannedPickupTime;
    private OffsetDateTime actualPickupTime;
    private OffsetDateTime plannedDropoffTime;
    private OffsetDateTime actualDropoffTime;
    private JourneyStatus status;
}
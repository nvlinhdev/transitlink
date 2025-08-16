package vn.edu.fpt.transitlink.passenger.service;

import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;

import java.security.Principal;
import java.util.UUID;

public interface PassengerService {

    PassengerDTO enterPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO importPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO getPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO deletePassengerData(UUID passengerId , Principal principal);
}

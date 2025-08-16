package vn.edu.fpt.transitlink.passenger.service;

import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;

import java.security.Principal;

public interface PassengerService {

    PassengerDTO enterPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO importPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO getPassengerData(PassengerDTO passengerData, Principal principal);
    void deletePassengerData(Principal principal);
}

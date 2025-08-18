package vn.edu.fpt.transitlink.user.service;

import vn.edu.fpt.transitlink.user.dto.DriverDTO;
import vn.edu.fpt.transitlink.user.dto.PassengerDTO;
import vn.edu.fpt.transitlink.user.dto.UserProfileDTO;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface UserService {

    //profile
    UserProfileDTO getMyProfile(Principal principal);
    UserProfileDTO getProfileById(String id, Principal principal);
    List<UserProfileDTO> searchProfile(String query, int page, int size, Principal principal);
    UserProfileDTO createProfile(UserProfileDTO profileData, Principal principal);
    UserProfileDTO updateBasicInfo(UserProfileDTO profileData, Principal principal);
    UserProfileDTO updatePhoneNumber(String token, Principal principal);
    void deleteMyProfile(Principal principal);

    //driver
    DriverDTO enterDriverData(DriverDTO driverDTO, Principal principal);
    DriverDTO importDriverData(DriverDTO driverDTO, Principal principal);
    DriverDTO deleteDriverData(UUID driverId, Principal principal);
    DriverDTO getListDriver(Principal principal);

    //passenger
    PassengerDTO enterPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO importPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO getPassengerData(PassengerDTO passengerData, Principal principal);
    PassengerDTO deletePassengerData(UUID passengerId , Principal principal);
}

package vn.edu.fpt.transitlink.user.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.user.mapper.PassengerMapper;
import vn.edu.fpt.transitlink.user.repository.PassengerRepository;
import vn.edu.fpt.transitlink.user.dto.DriverDTO;
import vn.edu.fpt.transitlink.user.dto.PassengerDTO;
import vn.edu.fpt.transitlink.user.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.user.mapper.DriverMapper;
import vn.edu.fpt.transitlink.user.repository.DriverRepository;
import vn.edu.fpt.transitlink.user.mapper.UserProfileMapper;
import vn.edu.fpt.transitlink.user.repository.UserProfileRepository;
import vn.edu.fpt.transitlink.user.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserProfileRepository profileRepository;
    private final UserProfileMapper userProfileMapper;

    private final DriverMapper driverMapper;
    private final DriverRepository driverRepository;

    private final PassengerMapper passengerMapper;
    private final PassengerRepository passengerRepository;


    public UserServiceImpl(
            UserProfileRepository profileRepository, UserProfileMapper userProfileMapper,
            DriverMapper driverMapper, DriverRepository driverRepository,
            PassengerMapper passengerMapper, PassengerRepository passengerRepository
    ) {
        this.profileRepository = profileRepository;
        this.userProfileMapper = userProfileMapper;
        this.driverMapper = driverMapper;
        this.driverRepository = driverRepository;
        this.passengerMapper = passengerMapper;
        this.passengerRepository = passengerRepository;
    }

    //profile
    @Override
    public UserProfileDTO getMyProfile(Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO getProfileById(String id, Principal principal) {
        return null;
    }

    @Override
    public List<UserProfileDTO> searchProfile(String query, int page, int size, Principal principal) {
        return List.of();
    }

    @Override
    public UserProfileDTO createProfile(UserProfileDTO profileData, Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO updateBasicInfo(UserProfileDTO profileData, Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO updatePhoneNumber(String token, Principal principal) {
        return null;
    }

    @Override
    public void deleteMyProfile(Principal principal) {

    }

    //driver
    @Override
    public DriverDTO enterDriverData(DriverDTO driverDTO, Principal principal) {
        return null;
    }

    @Override
    public DriverDTO importDriverData(DriverDTO driverDTO, Principal principal) {
        return null;
    }

    @Override
    public DriverDTO deleteDriverData(UUID driverId, Principal principal) {
        return null;
    }

    @Override
    public DriverDTO getListDriver(Principal principal) {
        return null;
    }


    //passenger
    @Override
    public PassengerDTO enterPassengerData(PassengerDTO passengerData, Principal principal) {
        return null;
    }

    @Override
    public PassengerDTO importPassengerData(PassengerDTO passengerData, Principal principal) {
        return null;
    }

    @Override
    public PassengerDTO getPassengerData(PassengerDTO passengerData, Principal principal) {
        return null;
    }

    @Override
    public PassengerDTO deletePassengerData(UUID passengerId, Principal principal) {
        return null;
    }
}

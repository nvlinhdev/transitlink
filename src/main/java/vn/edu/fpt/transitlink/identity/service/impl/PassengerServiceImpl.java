package vn.edu.fpt.transitlink.identity.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.mapper.PassengerMapper;
import vn.edu.fpt.transitlink.identity.repository.PassengerRepository;
import vn.edu.fpt.transitlink.identity.service.PassengerService;

import java.security.Principal;
import java.util.UUID;

@Service
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper passengerMapper;
    private final PassengerRepository passengerRepository;


    public PassengerServiceImpl(PassengerMapper passengerMapper, PassengerRepository passengerRepository) {
        this.passengerMapper = passengerMapper;
        this.passengerRepository = passengerRepository;
    }

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

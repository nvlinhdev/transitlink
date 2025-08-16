package vn.edu.fpt.transitlink.passenger.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;
import vn.edu.fpt.transitlink.passenger.mapper.PassengerMapper;
import vn.edu.fpt.transitlink.passenger.repository.PassengerRepository;
import vn.edu.fpt.transitlink.passenger.service.PassengerService;

import java.security.Principal;
import java.util.UUID;

@Service
public class PassengerServiceImpl implements PassengerService {
    private final PassengerMapper mapper;

    private final PassengerRepository passengerRepository;


    public PassengerServiceImpl(PassengerMapper mapper, PassengerRepository passengerRepository) {
        this.mapper = PassengerMapper.INSTANCE;
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

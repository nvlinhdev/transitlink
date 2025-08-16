package vn.edu.fpt.transitlink.passenger.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.passenger.mapper.PassengerMapper;
import vn.edu.fpt.transitlink.passenger.repository.PassengerRepository;
import vn.edu.fpt.transitlink.passenger.service.PassengerService;

@Service
public class PassengerServiceImpl implements PassengerService {
    private final PassengerMapper mapper;

    private final PassengerRepository passengerRepository;


    public PassengerServiceImpl(PassengerMapper mapper, PassengerRepository passengerRepository) {
        this.mapper = PassengerMapper.INSTANCE;
        this.passengerRepository = passengerRepository;
    }
}

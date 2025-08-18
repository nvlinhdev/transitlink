package vn.edu.fpt.transitlink.location.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.location.mapper.PassengerLocationMapper;
import vn.edu.fpt.transitlink.location.repository.PassengerLocationRepository;

@Service
public class PassengerLocationServiceImpl {
    private final PassengerLocationMapper mapper;

    private final PassengerLocationRepository passengerLocationRepository;

    public PassengerLocationServiceImpl(
            PassengerLocationMapper mapper,
            PassengerLocationRepository passengerLocationRepository
    ) {
        this.mapper = mapper;
        this.passengerLocationRepository = passengerLocationRepository;
    }
}

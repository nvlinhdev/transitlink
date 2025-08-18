package vn.edu.fpt.transitlink.trip.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.trip.mapper.PassengerLocationMapper;
import vn.edu.fpt.transitlink.trip.repository.PassengerLocationRepository;

@Service
public class PassengerLocationServiceImpl {
    private final PassengerLocationMapper mapper;

    private final PassengerLocationRepository routeRepository;

    public PassengerLocationServiceImpl(
            PassengerLocationMapper mapper,
            PassengerLocationRepository routeRepository
    ) {
        this.mapper = mapper;
        this.routeRepository = routeRepository;
    }
}

package vn.edu.fpt.transitlink.trip.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.trip.mapper.DriverLocationMapper;
import vn.edu.fpt.transitlink.trip.repository.DriverLocationRepository;

@Service
public class DriverLocationServiceImpl {

    private final DriverLocationMapper mapper;

    private final DriverLocationRepository driverLocationRepository;

    public DriverLocationServiceImpl(
            DriverLocationMapper mapper,
            DriverLocationRepository driverLocationRepository
    ) {
        this.mapper = mapper;
        this.driverLocationRepository = driverLocationRepository;
    }
}

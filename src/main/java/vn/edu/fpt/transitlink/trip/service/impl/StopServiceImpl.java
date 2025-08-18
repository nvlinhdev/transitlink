package vn.edu.fpt.transitlink.trip.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.trip.mapper.StopMapper;
import vn.edu.fpt.transitlink.trip.repository.StopRepository;
import vn.edu.fpt.transitlink.trip.service.StopService;

@Service
public class StopServiceImpl implements StopService {
    private final StopMapper mapper;

    private final StopRepository routeRepository;

    public StopServiceImpl(StopMapper mapper, StopRepository routeRepository) {
        this.mapper = mapper;
        this.routeRepository = routeRepository;
    }
}

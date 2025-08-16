package vn.edu.fpt.transitlink.route.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.route.dto.RouteDTO;
import vn.edu.fpt.transitlink.route.mapper.RouteMapper;
import vn.edu.fpt.transitlink.route.repository.RouteRepository;
import vn.edu.fpt.transitlink.route.service.RouteService;

import java.util.UUID;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteMapper mapper;

    private final RouteRepository routeRepository;


    public RouteServiceImpl(RouteMapper mapper, RouteRepository routeRepository) {
        this.mapper = RouteMapper.INSTANCE;
        this.routeRepository = routeRepository;
    }

    @Override
    public RouteDTO createRouteData(RouteDTO routeData) {
        return null;
    }

    @Override
    public RouteDTO viewRouteData(UUID routeId) {
        return null;
    }

    @Override
    public RouteDTO overwriteRouteData(RouteDTO routeData, UUID routeId) {
        return null;
    }

    @Override
    public RouteDTO deleteRouteData(UUID routeId) {
        return null;
    }
}

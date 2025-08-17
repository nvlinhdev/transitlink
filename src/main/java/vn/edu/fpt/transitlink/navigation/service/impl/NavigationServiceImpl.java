package vn.edu.fpt.transitlink.navigation.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.navigation.dto.NavigationDTO;
import vn.edu.fpt.transitlink.navigation.mapper.NavigationMapper;
import vn.edu.fpt.transitlink.navigation.repository.NavigationRepository;
import vn.edu.fpt.transitlink.navigation.service.NavigationService;

import java.util.UUID;

@Service
public class NavigationServiceImpl implements NavigationService {

    private final NavigationMapper mapper;
    private final NavigationRepository navigationRepository;

    public NavigationServiceImpl(NavigationMapper mapper, NavigationRepository navigationRepository) {
        this.mapper = NavigationMapper.INSTANCE;
        this.navigationRepository = navigationRepository;
    }

    @Override
    public NavigationDTO viewNavigation(NavigationDTO navigationDTO, UUID tripId) {
        return null;
    }

    @Override
    public NavigationDTO viewDriverTracking(NavigationDTO navigationDTO, UUID driverId) {
        return null;
    }
}

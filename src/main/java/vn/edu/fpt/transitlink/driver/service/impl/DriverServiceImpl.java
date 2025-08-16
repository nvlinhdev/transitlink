package vn.edu.fpt.transitlink.driver.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.driver.dto.DriverDTO;
import vn.edu.fpt.transitlink.driver.mapper.DriverMapper;
import vn.edu.fpt.transitlink.driver.repository.DriverRepository;
import vn.edu.fpt.transitlink.driver.service.DriverService;

import java.security.Principal;
import java.util.UUID;

@Service
public class DriverServiceImpl implements DriverService {
    private final DriverMapper mapper;

    private final DriverRepository driverRepository;


    public DriverServiceImpl(DriverMapper mapper, DriverRepository driverRepository) {
        this.mapper = DriverMapper.INSTANCE;
        this.driverRepository = driverRepository;
    }

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
}
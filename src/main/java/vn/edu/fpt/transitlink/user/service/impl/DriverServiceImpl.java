package vn.edu.fpt.transitlink.user.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.user.dto.DriverDTO;
import vn.edu.fpt.transitlink.user.mapper.DriverMapper;
import vn.edu.fpt.transitlink.user.repository.DriverRepository;
import vn.edu.fpt.transitlink.user.service.DriverService;

import java.security.Principal;
import java.util.UUID;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverMapper driverMapper;
    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverMapper driverMapper, DriverRepository driverRepository) {
        this.driverMapper = driverMapper;
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

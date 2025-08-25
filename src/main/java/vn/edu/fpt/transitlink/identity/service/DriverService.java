package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.DriverDTO;

import java.security.Principal;
import java.util.UUID;

public interface DriverService {
    DriverDTO enterDriverData(DriverDTO driverDTO, Principal principal);
    DriverDTO importDriverData(DriverDTO driverDTO, Principal principal);
    DriverDTO deleteDriverData(UUID driverId, Principal principal);
    DriverDTO getListDriver(Principal principal);
}

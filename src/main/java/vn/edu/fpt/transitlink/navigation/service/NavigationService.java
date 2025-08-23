package vn.edu.fpt.transitlink.navigation.service;

import vn.edu.fpt.transitlink.navigation.dto.NavigationDTO;

import java.util.UUID;

public interface NavigationService {

    NavigationDTO viewNavigation(NavigationDTO navigationDTO, UUID tripId);
    NavigationDTO viewDriverTracking(NavigationDTO navigationDTO, UUID driverId);
}

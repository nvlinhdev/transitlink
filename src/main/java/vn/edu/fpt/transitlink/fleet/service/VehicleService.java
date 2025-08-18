package vn.edu.fpt.transitlink.fleet.service;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;

import java.security.Principal;
import java.util.UUID;

public interface VehicleService {

    VehicleDTO enterVehicleData(VehicleDTO vehicleData, Principal principal);
    VehicleDTO importVehicleData(VehicleDTO vehicleData, Principal principal);
    VehicleDTO viewVehicleList(VehicleDTO vehicleData, Principal principal);
    VehicleDTO deleteVehicleData(UUID vehicleId , Principal principal);

}

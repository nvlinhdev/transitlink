package vn.edu.fpt.transitlink.vehicle.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.vehicle.dto.VehicleDTO;
import vn.edu.fpt.transitlink.vehicle.mapper.VehicleMapper;
import vn.edu.fpt.transitlink.vehicle.repository.VehicleRepository;
import vn.edu.fpt.transitlink.vehicle.service.VehicleService;

import java.security.Principal;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper mapper;
    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleMapper mapper, VehicleRepository vehicleRepository) {
        this.mapper = VehicleMapper.INSTANCE;
        this.vehicleRepository = vehicleRepository;
    }


    @Override
    public VehicleDTO enterVehicleData(VehicleDTO vehicleData, Principal principal) {
        return null;
    }

    @Override
    public VehicleDTO importVehicleData(VehicleDTO vehicleData, Principal principal) {
        return null;
    }

    @Override
    public VehicleDTO viewVehicleList(VehicleDTO vehicleData, Principal principal) {
        return null;
    }

    @Override
    public VehicleDTO deleteVehicleData(UUID vehicleId, Principal principal) {
        return null;
    }
}

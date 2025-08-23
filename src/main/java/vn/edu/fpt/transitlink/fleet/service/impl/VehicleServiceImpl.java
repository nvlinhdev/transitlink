package vn.edu.fpt.transitlink.fleet.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.mapper.DepotMapper;
import vn.edu.fpt.transitlink.fleet.repository.DepotRepository;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;

import java.security.Principal;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final DepotMapper depotMapper;
    private final DepotRepository depotRepository;

    public VehicleServiceImpl(DepotMapper depotMapper, DepotRepository depotRepository) {
        this.depotMapper = depotMapper;
        this.depotRepository = depotRepository;
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

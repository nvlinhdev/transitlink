package vn.edu.fpt.transitlink.fleet.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.mapper.DepotMapper;
import vn.edu.fpt.transitlink.fleet.repository.DepotRepository;
import vn.edu.fpt.transitlink.fleet.service.FleetService;
import vn.edu.fpt.transitlink.fleet.mapper.VehicleMapper;
import vn.edu.fpt.transitlink.fleet.repository.VehicleRepository;

import java.security.Principal;
import java.util.UUID;

@Service
public class FleetServiceImpl implements FleetService {

    private final VehicleMapper mapper;
    private final VehicleRepository vehicleRepository;

    private final DepotMapper depotMapper;
    private final DepotRepository depotRepository;

    public FleetServiceImpl(
            VehicleMapper mapper, VehicleRepository vehicleRepository,
            DepotMapper depotMapper, DepotRepository depotRepository
    ) {
        this.mapper = mapper;
        this.vehicleRepository = vehicleRepository;
        this.depotMapper = depotMapper;
        this.depotRepository = depotRepository;
    }

    //Depot

    //Vehicle
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

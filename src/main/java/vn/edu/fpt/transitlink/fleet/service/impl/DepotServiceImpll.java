package vn.edu.fpt.transitlink.fleet.service.impl;

import vn.edu.fpt.transitlink.fleet.mapper.DepotMapper;
import vn.edu.fpt.transitlink.fleet.repository.DepotRepository;
import vn.edu.fpt.transitlink.fleet.service.DepotService;

public class DepotServiceImpll implements DepotService {

    private final DepotMapper depotMapper;
    private final DepotRepository depotRepository;

    public DepotServiceImpll(DepotMapper depotMapper, DepotRepository depotRepository) {
        this.depotMapper = depotMapper;
        this.depotRepository = depotRepository;
    }
}

package vn.edu.fpt.transitlink.fleet.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.fleet.mapper.DepotMapper;
import vn.edu.fpt.transitlink.fleet.repository.DepotRepository;
import vn.edu.fpt.transitlink.fleet.service.DepotService;

@Service
public class DepotServiceImpl implements DepotService {

    private final DepotMapper depotMapper;
    private final DepotRepository depotRepository;

    public DepotServiceImpl(DepotMapper depotMapper, DepotRepository depotRepository) {
        this.depotMapper = depotMapper;
        this.depotRepository = depotRepository;
    }
}

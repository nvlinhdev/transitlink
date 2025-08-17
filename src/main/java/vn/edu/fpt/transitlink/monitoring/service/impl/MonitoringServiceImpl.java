package vn.edu.fpt.transitlink.monitoring.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.monitoring.dto.MonitoringDTO;
import vn.edu.fpt.transitlink.monitoring.mapper.MonitoringMapper;
import vn.edu.fpt.transitlink.monitoring.repository.MonitoringRepository;
import vn.edu.fpt.transitlink.monitoring.service.MonitoringService;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    private final MonitoringMapper mapper;

    private final MonitoringRepository monitoringRepository;


    public MonitoringServiceImpl(MonitoringMapper mapper, MonitoringRepository monitoringRepository) {
        this.mapper = MonitoringMapper.INSTANCE;
        this.monitoringRepository = monitoringRepository;
    }

    @Override
    public MonitoringDTO viewMonitoring(MonitoringDTO monitoringDTO) {
        return null;
    }
}

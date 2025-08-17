package vn.edu.fpt.transitlink.schedule.service.impl;


import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.schedule.dto.ScheduleDTO;
import vn.edu.fpt.transitlink.schedule.mapper.ScheduleMapper;
import vn.edu.fpt.transitlink.schedule.repository.ScheduleRepository;
import vn.edu.fpt.transitlink.schedule.service.ScheduleService;

import java.util.UUID;
@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleMapper mapper;

    private final ScheduleRepository scheduleRepository;


    public ScheduleServiceImpl(ScheduleMapper mapper, ScheduleRepository scheduleRepository) {
        this.mapper = ScheduleMapper.INSTANCE;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ScheduleDTO viewScheduleList(String routeId) {
        return null;
    }

    @Override
    public ScheduleDTO createScheduleData(ScheduleDTO scheduleData) {
        return null;
    }

    @Override
    public ScheduleDTO overrideSchedule(UUID scheduleId, ScheduleDTO scheduleData) {
        return null;
    }

    @Override
    public ScheduleDTO deleteScheduleData(UUID scheduleId) {
        return null;
    }
}

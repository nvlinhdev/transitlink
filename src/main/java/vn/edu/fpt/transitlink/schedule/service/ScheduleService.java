package vn.edu.fpt.transitlink.schedule.service;

import vn.edu.fpt.transitlink.schedule.dto.ScheduleDTO;

import java.util.UUID;

public interface ScheduleService {

    ScheduleDTO viewScheduleList(String routeId);
    ScheduleDTO createScheduleData(ScheduleDTO scheduleData);
    ScheduleDTO overrideSchedule(UUID scheduleId, ScheduleDTO scheduleData);
    ScheduleDTO deleteScheduleData(UUID scheduleId);
}

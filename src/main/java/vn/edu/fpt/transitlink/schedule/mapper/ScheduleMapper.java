package vn.edu.fpt.transitlink.schedule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.schedule.dto.ScheduleDTO;
import vn.edu.fpt.transitlink.schedule.entity.Schedule;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    public static final ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    public ScheduleDTO toDTO(Schedule schedule);

}

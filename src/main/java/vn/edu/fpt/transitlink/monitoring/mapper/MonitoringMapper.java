package vn.edu.fpt.transitlink.monitoring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.monitoring.dto.MonitoringDTO;
import vn.edu.fpt.transitlink.monitoring.entity.MonitoringDashboard;

@Mapper(componentModel = "spring")
public interface MonitoringMapper {
    public static final MonitoringMapper INSTANCE = Mappers.getMapper(MonitoringMapper.class);

    public MonitoringDTO toDTO(MonitoringDashboard monitoringDashboard);

}

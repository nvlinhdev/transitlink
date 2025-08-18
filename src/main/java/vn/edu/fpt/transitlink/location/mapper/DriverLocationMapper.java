package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.location.dto.DriverLocationDTO;
import vn.edu.fpt.transitlink.location.entity.DriverLocation;

@Mapper(componentModel = "spring")
public interface DriverLocationMapper {
    public static final DriverLocationMapper INSTANCE = Mappers.getMapper(DriverLocationMapper.class);

    public DriverLocationDTO toDTO(DriverLocation driverLocation);
}

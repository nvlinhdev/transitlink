package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.location.dto.DriverLocationDTO;
import vn.edu.fpt.transitlink.location.entity.DriverLocation;

@Mapper(componentModel = "spring")
public interface DriverLocationMapper {
    DriverLocationDTO toDTO(DriverLocation entity);
}

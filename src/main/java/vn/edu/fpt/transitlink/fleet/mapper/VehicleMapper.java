package vn.edu.fpt.transitlink.fleet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.entity.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    public static final VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    public VehicleDTO toDTO(Vehicle vehicle);
}

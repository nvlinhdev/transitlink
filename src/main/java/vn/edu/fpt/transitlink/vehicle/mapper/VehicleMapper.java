package vn.edu.fpt.transitlink.vehicle.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.vehicle.dto.VehicleDTO;
import vn.edu.fpt.transitlink.vehicle.entity.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    public static final VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    public VehicleDTO toDTO(Vehicle vehicle);
}

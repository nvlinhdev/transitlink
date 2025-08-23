package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.location.dto.PassengerLocationDTO;
import vn.edu.fpt.transitlink.location.entity.PassengerLocation;

@Mapper(componentModel = "spring")
public interface PassengerLocationMapper {
    public static final PassengerLocationMapper INSTANCE = Mappers.getMapper(PassengerLocationMapper.class);

    public PassengerLocationDTO toDTO(PassengerLocation passengerLocation);
}

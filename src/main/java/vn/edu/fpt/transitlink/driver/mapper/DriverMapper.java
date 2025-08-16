package vn.edu.fpt.transitlink.driver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.driver.dto.DriverDTO;
import vn.edu.fpt.transitlink.driver.entity.Driver;
import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;
import vn.edu.fpt.transitlink.passenger.entity.Passenger;
import vn.edu.fpt.transitlink.passenger.mapper.PassengerMapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    public static final DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);

    public DriverDTO toDTO(Driver driver);
}

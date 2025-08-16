package vn.edu.fpt.transitlink.passenger.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.passenger.entity.Passenger;
import vn.edu.fpt.transitlink.passenger.dto.PassengerDTO;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    public static final PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    public PassengerDTO toDTO(Passenger passenger);
}

package vn.edu.fpt.transitlink.user.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.user.dto.PassengerDTO;
import vn.edu.fpt.transitlink.user.entity.Passenger;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    public static final PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    public PassengerDTO toDTO(Passenger passenger);
}

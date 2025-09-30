package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.location.dto.PassengerLocationDTO;
import vn.edu.fpt.transitlink.location.entity.PassengerLocation;

@Mapper(componentModel = "spring")
public interface PassengerLocationMapper {
    PassengerLocationDTO toDTO(PassengerLocation entity);
}

package vn.edu.fpt.transitlink.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.user.dto.DriverDTO;
import vn.edu.fpt.transitlink.user.entity.Driver;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    public static final DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);

    public DriverDTO toDTO(Driver driver);
}

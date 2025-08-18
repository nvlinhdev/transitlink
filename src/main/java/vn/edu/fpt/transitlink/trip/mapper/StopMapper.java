package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.trip.dto.StopDTO;
import vn.edu.fpt.transitlink.trip.entity.Stop;

@Mapper(componentModel = "spring")
public interface StopMapper {
    public static final StopMapper INSTANCE = Mappers.getMapper(StopMapper.class);

    public StopDTO toDTO(Stop stop);
}

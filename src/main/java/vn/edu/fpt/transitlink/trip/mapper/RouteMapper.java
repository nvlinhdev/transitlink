package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.trip.dto.RouteDTO;
import vn.edu.fpt.transitlink.trip.entity.Route;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    public static final RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    public RouteDTO toDTO(Route route);

}

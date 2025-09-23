package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.trip.dto.*;
import vn.edu.fpt.transitlink.trip.entity.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StopMapper.class})
public interface RouteMapper {
    RouteDTO toDTO(Route route);
    List<RouteDTO> toDTOList(List<Route> routes);
}

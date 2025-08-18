package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.entity.Place;

@Mapper(componentModel = "spring")

public interface PlaceMapper {
    public static final PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    public PlaceDTO toDTO(Place place);
}

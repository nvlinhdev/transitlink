package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.entity.Place;
import vn.edu.fpt.transitlink.location.entity.PlaceDocument;

@Mapper(componentModel = "spring")
public interface PlaceMapper {
    PlaceDTO toDTO(Place place);
    Place toEntity(PlaceDTO placeDTO);
    PlaceDocument toDocument(Place place);
    PlaceDTO toDTO(PlaceDocument document);
}

package vn.edu.fpt.transitlink.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.entity.Place;
import vn.edu.fpt.transitlink.location.entity.PlaceDocument;
import vn.edu.fpt.transitlink.location.request.ImportPlaceRequest;

@Mapper(componentModel = "spring")
public interface PlaceMapper {
    PlaceDTO toDTO(Place place);
    Place toEntity(PlaceDTO placeDTO);
    PlaceDocument toDocument(Place place);
    PlaceDTO toDTO(PlaceDocument document);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Place toEntity(ImportPlaceRequest request);
}

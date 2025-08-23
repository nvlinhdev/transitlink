package vn.edu.fpt.transitlink.fleet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
import vn.edu.fpt.transitlink.fleet.entity.Depot;

@Mapper(componentModel = "spring")
public interface DepotMapper {
    public static final DepotMapper INSTANCE = Mappers.getMapper(DepotMapper.class);

    public DepotDTO toDTO(Depot depot);
}

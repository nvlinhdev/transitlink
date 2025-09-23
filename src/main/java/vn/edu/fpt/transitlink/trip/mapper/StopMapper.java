package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.fpt.transitlink.trip.dto.StopDTO;
import vn.edu.fpt.transitlink.trip.entity.Stop;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StopJourneyMappingMapper.class})
public interface StopMapper {
    @Mapping(target = "passengers", source = "stopJourneyMappings")
    StopDTO toStopDTO(Stop stop);

    List<StopDTO> toStopDTOList(List<Stop> stops);
}

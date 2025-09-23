package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.fpt.transitlink.trip.dto.PassengerOnStopDTO;
import vn.edu.fpt.transitlink.trip.entity.StopJourneyMapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StopJourneyMappingMapper {
    @Mapping(target = "passengerJourneyInfo", source = "passengerJourney")
    PassengerOnStopDTO toDto(StopJourneyMapping mapping);

    List<PassengerOnStopDTO> toDtoList(List<StopJourneyMapping> mappings);
}

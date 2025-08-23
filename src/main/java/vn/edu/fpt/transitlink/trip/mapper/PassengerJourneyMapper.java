package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourney;

@Mapper(componentModel = "spring")
public interface PassengerJourneyMapper {
    public static final PassengerJourneyMapper INSTANCE = Mappers.getMapper(PassengerJourneyMapper.class);

    public PassengerJourneyDTO toDTO(PassengerJourney passengerJourney);
}

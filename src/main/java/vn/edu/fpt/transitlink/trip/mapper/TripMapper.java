package vn.edu.fpt.transitlink.trip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.trip.dto.DropoffDTO;
import vn.edu.fpt.transitlink.trip.dto.PickupDTO;
import vn.edu.fpt.transitlink.trip.dto.TripCheckinDTO;
import vn.edu.fpt.transitlink.trip.dto.TripCheckoutDTO;
import vn.edu.fpt.transitlink.trip.entity.Dropoff;
import vn.edu.fpt.transitlink.trip.entity.Pickup;
import vn.edu.fpt.transitlink.trip.entity.TripCheckin;
import vn.edu.fpt.transitlink.trip.entity.TripCheckout;

@Mapper(componentModel = "spring")
public interface TripMapper {

    public static final TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

    public TripCheckinDTO toDTO(TripCheckin tripCheckin);

    public TripCheckoutDTO toDTO(TripCheckout tripCheckout);

    public DropoffDTO toDTO(Dropoff dropoff);

    public PickupDTO toDTO(Pickup pickup);
}

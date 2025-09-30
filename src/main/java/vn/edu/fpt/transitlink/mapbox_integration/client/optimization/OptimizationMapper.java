package vn.edu.fpt.transitlink.mapbox_integration.client.optimization;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request.*;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class OptimizationMapper {
    public static OptimizationRequestBody buildRoutingProblem(
            List<PassengerJourneyDTO> passengerJourneys,
            List<VehicleDTO> vehicles
    ) {

        if (passengerJourneys == null || passengerJourneys.isEmpty()) {
            throw new IllegalArgumentException("passengerJourneys cannot be null or empty");
        }

        if (vehicles == null || vehicles.isEmpty()) {
            throw new IllegalArgumentException("vehicles cannot be null or empty");
        }

        Set<PlaceDTO> uniquePlaces = passengerJourneys.stream()
                .flatMap(journey -> Set.of(journey.getPickupPlace(), journey.getDropoffPlace()).stream())
                .collect(toSet());

        // add depot places to uniquePlaces
        vehicles.stream()
                .map(vehicle -> vehicle.depot().place())
                .forEach(uniquePlaces::add);

        List<Location> locations = uniquePlaces.stream()
                .map(
                        place -> new Location(
                                place.id().toString(),
                                List.of(place.longitude(), place.latitude())
                        )
                ).toList();

        List<Shipment> shipments = passengerJourneys.stream()
                .map(
                        journey -> {
                            if (journey.getJourneyType() == JourneyType.POST_TRIP) {
                                return new Shipment(
                                        journey.getId().toString(),
                                        journey.getPickupPlace().id().toString(),
                                        journey.getDropoffPlace().id().toString(),
                                        Map.of("seats", journey.getSeatCount()),
                                        0,
                                        0,
                                        List.of(new TimeWindow(
                                                journey.getMainStopArrivalTime().minusMinutes(10).toInstant(),
                                                journey.getMainStopArrivalTime().toInstant(),
                                                "strict"
                                        )),
                                        null
                                );
                            } else if (journey.getJourneyType() == JourneyType.PRE_TRIP){
                                return new Shipment(
                                        journey.getId().toString(),
                                        journey.getPickupPlace().id().toString(),
                                        journey.getDropoffPlace().id().toString(),
                                        Map.of("seats", journey.getSeatCount()),
                                        0,
                                        0,
                                        null,
                                        List.of(new TimeWindow(
                                                journey.getMainStopArrivalTime().minusMinutes(10).toInstant(),
                                                journey.getMainStopArrivalTime().toInstant(),
                                                "strict"
                                        ))


                                );
                            }
                            throw new IllegalStateException("unexpected journey type");
                        }
                ).toList();

        List<Vehicle> vehicleList = vehicles.stream()
                .map(
                        vehicle -> new Vehicle(
                                vehicle.id().toString(),
                                "mapbox/driving",
                                vehicle.depot().place().id().toString(),
                                null,
                                Map.of("seats", vehicle.capacity())
                        )
                ).toList();

        return new OptimizationRequestBody(
                1,
                locations,
                vehicleList,
                shipments,
                new Options(List.of("min-total-travel-duration"))
        );
    }
}

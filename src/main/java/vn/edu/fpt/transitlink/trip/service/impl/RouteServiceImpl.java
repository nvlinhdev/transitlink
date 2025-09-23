package vn.edu.fpt.transitlink.trip.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.dto.RouteDTO;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourney;
import vn.edu.fpt.transitlink.trip.entity.Route;
import vn.edu.fpt.transitlink.trip.entity.Stop;
import vn.edu.fpt.transitlink.trip.entity.StopJourneyMapping;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;
import vn.edu.fpt.transitlink.trip.mapper.RouteMapper;
import vn.edu.fpt.transitlink.trip.repository.RouteRepository;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;
import vn.edu.fpt.transitlink.trip.request.UpdatePassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.service.PassengerJourneyService;
import vn.edu.fpt.transitlink.trip.service.RouteService;
import vn.edu.fpt.transitlink.trip.spi.RouteOptimizationProvider;
import vn.edu.fpt.transitlink.trip.spi.dto.OptimizationResult;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RouteServiceImpl implements RouteService {

    private final RouteMapper routeMapper;

    private final RouteRepository routeRepository;

    private final RouteOptimizationProvider routeOptimizationProvider;
    private final PassengerJourneyService passengerJourneyService;
    private final VehicleService vehicleService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public List<RouteDTO> optimizeRoute(OptimizationRouteRequest request) {
        List<PassengerJourneyDTO> passengerJourneyDTOList = passengerJourneyService.getAllPassengerJourneysByIds(request.passengerJourneyIds());
        List<VehicleDTO> VehiclesDTOList = vehicleService.getAllVehiclesByIds(request.vehicleIds());

        OptimizationResult result = routeOptimizationProvider.optimizeRoutes(passengerJourneyDTOList, RouteType.PRE_TRIP, VehiclesDTOList);

        Map<UUID, PassengerJourneyDTO> mapIdToPassengerJourneyDTO = passengerJourneyDTOList.stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getId(), v), HashMap::putAll);

        Map<UUID, VehicleDTO> mapIdToVehicleDTO = VehiclesDTOList.stream()
                .collect(HashMap::new, (m, v) -> m.put(v.id(), v), HashMap::putAll);

        result.getPassengerJourneys().stream()
                .forEach(passengerJourneyData -> {
                    Optional.ofNullable(mapIdToPassengerJourneyDTO.get(passengerJourneyData.getPassengerJourneyId()))
                            .ifPresent(dto -> {
                                dto.setGeometry(passengerJourneyData.getGeometry());
                                dto.setPlannedPickupTime(passengerJourneyData.getPlannedDepartureTime());
                                dto.setPlannedDropoffTime(passengerJourneyData.getPlannedArrivalTime());
                            });
                });

        List<Route> routes = result.getRoutes().stream()
                .map(routeData -> {
                    Route route = new Route();
                    route.setGeometry(routeData.getGeometry());
                    route.setEstimatedDistanceKm(routeData.getEstimatedDistanceKm());
                    route.setEstimatedDurationMin(routeData.getEstimatedDurationMin());
                    route.setPlannedArrivalTime(routeData.getPlannedArrivalTime());
                    route.setPlannedDepartureTime(routeData.getPlannedDepartureTime());
                    route.setType(request.routeType());
                    route.setStatus(RouteStatus.PLANNED);
                    route.setDirectionUrl(routeData.getDirectionUrl());
                    route.setVehicleId(routeData.getVehicleId());
                    List<Stop> stops = routeData.getStops().stream()
                            .map(stopData -> {
                                Stop stop = new Stop();
                                stop.setLatitude(stopData.getLatitude());
                                stop.setLongitude(stopData.getLongitude());
                                stop.setSequence(stopData.getSequence());
                                stop.setPlannedDepartureTime(stopData.getPlannedDepartureTime());
                                stop.setRoute(route);
                                List<StopJourneyMapping> mappings = stopData.getPassengerOnStopDatas().stream()
                                        .map(passengerOnStopData -> {
                                            PassengerJourneyDTO passengerJourneyDTO = mapIdToPassengerJourneyDTO.get(
                                                    passengerOnStopData.getPassengerJourneyId()
                                            );
                                            UpdatePassengerJourneyRequest updateRequest = new UpdatePassengerJourneyRequest(
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    passengerJourneyDTO.getGeometry(),
                                                    passengerJourneyDTO.getPlannedPickupTime(),
                                                    null,
                                                    passengerJourneyDTO.getPlannedDropoffTime(),
                                                    null,
                                                    JourneyStatus.SCHEDULED

                                            );
                                            PassengerJourneyDTO updatedJourney = passengerJourneyService.updatePassengerJourney(passengerOnStopData.getPassengerJourneyId(),
                                                    updateRequest);
                                            return new StopJourneyMapping(
                                                    null,
                                                    passengerOnStopData.getAction(),
                                                    stop,
                                                    entityManager.getReference(PassengerJourney.class, updatedJourney.getId())
                                            );
                                        }).toList();
                                stop.setStopJourneyMappings(mappings);
                                return stop;

                            }).toList();
                    route.setStops(stops);
                    return route;
                }).toList();

        List<RouteDTO> routeDTOs = routeMapper.toDTOList(routeRepository.saveAll(routes));

// enrich dữ liệu hành khách trong routeDTOs
        routeDTOs.forEach(routeDTO ->
                routeDTO.stops().forEach(stopDTO ->
                        stopDTO.passengers().forEach(passengerOnStopDTO -> {
                            PassengerJourneyDTO passengerJourneyDTO =
                                    mapIdToPassengerJourneyDTO.get(passengerOnStopDTO.passengerJourneyInfo().getId());
                            if (passengerJourneyDTO != null) {
                                passengerOnStopDTO.passengerJourneyInfo().setFirstName(passengerJourneyDTO.getPassenger().account().firstName());
                                passengerOnStopDTO.passengerJourneyInfo().setLastName(passengerJourneyDTO.getPassenger().account().lastName());
                                passengerOnStopDTO.passengerJourneyInfo().setPhoneNumber(passengerJourneyDTO.getPassenger().account().phoneNumber());
                                passengerOnStopDTO.passengerJourneyInfo().setEmail(passengerJourneyDTO.getPassenger().account().email());
                            }
                        })
                )
        );

        return routeDTOs;
    }
}

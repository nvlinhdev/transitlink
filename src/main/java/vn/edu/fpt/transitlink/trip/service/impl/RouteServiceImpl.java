package vn.edu.fpt.transitlink.trip.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;
import vn.edu.fpt.transitlink.identity.dto.DriverInfo;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.service.DriverService;
import vn.edu.fpt.transitlink.identity.service.PassengerService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.trip.dto.*;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourney;
import vn.edu.fpt.transitlink.trip.entity.Route;
import vn.edu.fpt.transitlink.trip.entity.Stop;
import vn.edu.fpt.transitlink.trip.entity.StopJourneyMapping;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteStatus;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;
import vn.edu.fpt.transitlink.trip.exception.TripErrorCode;
import vn.edu.fpt.transitlink.trip.mapper.RouteMapper;
import vn.edu.fpt.transitlink.trip.repository.RouteRepository;
import vn.edu.fpt.transitlink.trip.request.OptimizationRouteRequest;
import vn.edu.fpt.transitlink.trip.request.UpdatePassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.service.PassengerJourneyService;
import vn.edu.fpt.transitlink.trip.service.RouteService;
import vn.edu.fpt.transitlink.trip.spi.RouteOptimizationProvider;
import vn.edu.fpt.transitlink.trip.spi.dto.OptimizationResult;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RouteServiceImpl implements RouteService {

    private final RouteMapper routeMapper;

    private final RouteRepository routeRepository;
    private final RouteOptimizationProvider routeOptimizationProvider;
    private final PassengerJourneyService passengerJourneyService;
    private final VehicleService vehicleService;
    private final PassengerService passengerService;
    private final DriverService driverService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public OptimizationResultDTO optimizeRoute(OptimizationRouteRequest request) {
        List<PassengerJourneyDTO> passengerJourneyDTOList = passengerJourneyService.getAllPassengerJourneysByIds(request.passengerJourneyIds());
        List<VehicleDTO> VehiclesDTOList = vehicleService.getAllVehiclesByIds(request.vehicleIds());

        OptimizationResult result = routeOptimizationProvider.optimizeRoutes(passengerJourneyDTOList, RouteType.PRE_TRIP, VehiclesDTOList);

        Map<UUID, PassengerJourneyDTO> mapIdToPassengerJourneyDTO = passengerJourneyDTOList.stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getId(), v), HashMap::putAll);

        Map<UUID, VehicleDTO> mapIdToVehicleDTO = VehiclesDTOList.stream()
                .collect(HashMap::new, (m, v) -> m.put(v.id(), v), HashMap::putAll);

        List<PassengerJourneyInfo> unservedPassengerJourneys = result.getDroppedPassengerJourneyIds().stream()
                .map(passengerJourneyData -> {
                    PassengerJourneyDTO dto = mapIdToPassengerJourneyDTO.get(passengerJourneyData);
                    return new PassengerJourneyInfo(
                            dto.getId(),
                            dto.getPassenger().account().firstName(),
                            dto.getPassenger().account().lastName(),
                            dto.getPassenger().account().phoneNumber(),
                            dto.getPassenger().account().email(),
                            dto.getSeatCount()
                    );
                }).toList();

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

        List<RouteSummaryDTO> routeSummaries = routeDTOs.stream()
                .map(routeData -> {
                    VehicleDTO vehicleDTO = mapIdToVehicleDTO.get(routeData.vehicleId());
                    return new RouteSummaryDTO(
                            routeData.id(),
                            routeData.plannedDepartureTime(),
                            routeData.plannedArrivalTime(),
                            routeData.estimatedDistanceKm(),
                            routeData.estimatedDurationMin(),
                            vehicleDTO
                    );
                }).toList();

        return new OptimizationResultDTO(
                request.passengerJourneyIds().size(),
                request.vehicleIds().size(),
                routes.size(),
                unservedPassengerJourneys.size(),
                routeSummaries,
                unservedPassengerJourneys);
    }

    @Transactional
    @Override
    public List<RouteSummaryDTO> getRoutes(int page, int size, UUID createdUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("plannedDepartureTime").descending());
        List<Route> routes = routeRepository.findAllByCreatedByAndIsDeletedFalse(createdUserId, pageable).getContent();

        List<UUID> vehicleIds = routes.stream().map(Route::getVehicleId).toList();

        Map<UUID, VehicleDTO> vehicleMap = vehicleService.getAllVehiclesByIds(vehicleIds).stream()
                .collect(Collectors.toMap(VehicleDTO::id, v -> v));

        return routes.stream()
                .map(route ->
                        new RouteSummaryDTO(
                                route.getId(),
                                route.getPlannedDepartureTime(),
                                route.getPlannedArrivalTime(),
                                route.getEstimatedDistanceKm(),
                                route.getEstimatedDurationMin(),
                                vehicleMap.get(route.getVehicleId())
                        )
                ).toList();
    }

    @Override
    public long countRoutes(UUID createdUserId) {
        return routeRepository.countByCreatedByAndIsDeletedFalse(createdUserId);
    }

    @Transactional
    @Override
    public RouteDetailDTO getRoute(UUID routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new BusinessException(TripErrorCode.ROUTE_NOT_FOUND));
        VehicleDTO vehicle = vehicleService.getVehicle(route.getVehicleId());
        DriverInfo driver = driverService.getDriverInfoById(route.getDriverId());

        return new RouteDetailDTO(
                route.getId(),
                driver,
                vehicle,
                route.getEstimatedDistanceKm(),
                route.getEstimatedDurationMin(),
                route.getPlannedDepartureTime(),
                route.getCheckinTime(),
                route.getPlannedArrivalTime(),
                route.getCheckoutTime(),
                route.getGeometry(),
                route.getType(),
                route.getStatus(),
                mapStops(route.getStops())
        );
    }

    @Override
    public void assignDriverToRoute(UUID driverId, UUID routeId) {
        routeRepository.findById(routeId).ifPresent(route -> {
            //check driver đã có route nào vào thời gian này chưa
            if (routeRepository.existsOverlappingRoute(driverId, route.getPlannedDepartureTime(), route.getPlannedArrivalTime())) {
                throw new BusinessException(TripErrorCode.DRIVER_HAS_OVERLAPPING_ROUTE);
            }
            //assign driver
            route.setDriverId(driverId);
            routeRepository.save(route);
        });
    }

    @Override
    public void publishRoute(UUID routeId) {
        routeRepository.findById(routeId).ifPresent(route -> {
            //check route đã có driver chưa
            if (route.getDriverId() == null) {
                throw new BusinessException(TripErrorCode.ROUTE_UPDATE_FAILED, "Cannot publish route without assigned driver");
            }
            // Chỉ publish những route có trạng thái PLANNED
            if (route.getStatus() != RouteStatus.PLANNED) {
                throw new BusinessException(TripErrorCode.ROUTE_UPDATE_FAILED, "Only routes with status PLANNED can be published");
            }
            route.setStatus(RouteStatus.PUBLISHED);
            routeRepository.save(route);
        });
        // Todo: Gửi notification cho lái xe
        // Todo: Gửi notification cho hành khách
    }

    @Override
    public List<DriverRouteSummaryDTO> getAllDriverRouteByDriverId(UUID driverRouteId) {
        List<Route> routes = routeRepository.findAllByDriverId(driverRouteId);

        return routes.stream()
                .map(route -> new DriverRouteSummaryDTO(
                        route.getId(),
                        route.getEstimatedDistanceKm(),
                        route.getEstimatedDurationMin(),
                        route.getPlannedDepartureTime(),
                        route.getPlannedArrivalTime(),
                        route.getType()
                ))
                .toList();
    }

    @Transactional
    @Override
    public DriverRouteDetailDTO getDriverRouteById(UUID routeId) {
        Route route = routeRepository.findByIdAndStatus(routeId, RouteStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(TripErrorCode.ROUTE_NOT_FOUND));

        VehicleDTO vehicle = vehicleService.getVehicle(route.getVehicleId());

        // 1. Gom toàn bộ passengerId
        List<UUID> passengerIds = route.getStops().stream()
                .flatMap(stop -> stop.getStopJourneyMappings().stream())
                .map(m -> m.getPassengerJourney().getPassengerId())
                .distinct()
                .toList();

        // 2. Lấy 1 lần tất cả Passenger theo danh sách id
        Map<UUID, PassengerDTO> passengerMap = passengerService
                .getPassengersByIds(passengerIds)
                .stream()
                .collect(Collectors.toMap(PassengerDTO::id, p -> p));

        // 3. Dựng StopDTO + PassengerOnStopDTO
        List<StopDTO> stops = route.getStops().stream()
                .map(stop -> {
                    List<PassengerOnStopDTO> passengerOnStopDTOS = stop.getStopJourneyMappings().stream()
                            .map(stopJourneyMapping -> {
                                PassengerDTO passenger = passengerMap.get(
                                        stopJourneyMapping.getPassengerJourney().getPassengerId()
                                );
                                PassengerJourneyInfo passengerJourneyInfo = new PassengerJourneyInfo(
                                        passenger.id(),
                                        passenger.account().firstName(),
                                        passenger.account().lastName(),
                                        passenger.account().phoneNumber(),
                                        passenger.account().email(),
                                        stopJourneyMapping.getPassengerJourney().getSeatCount()
                                );
                                return new PassengerOnStopDTO(
                                        passengerJourneyInfo,
                                        stopJourneyMapping.getAction()
                                );
                            }).toList();
                    return new StopDTO(
                            stop.getId(),
                            stop.getLatitude(),
                            stop.getLongitude(),
                            stop.getSequence(),
                            stop.getPlannedDepartureTime(),
                            stop.getActualDepartureTime(),
                            passengerOnStopDTOS
                    );
                }).toList();

        return new DriverRouteDetailDTO(
                route.getId(),
                vehicle,
                route.getEstimatedDistanceKm(),
                route.getEstimatedDurationMin(),
                route.getPlannedDepartureTime(),
                route.getPlannedArrivalTime(),
                route.getGeometry(),
                route.getDirectionUrl(),
                route.getType(),
                stops
        );
    }

    private List<StopDTO> mapStops(List<Stop> stops) {
        if (stops == null) {
            return List.of();
        }

        // 1. Gom toàn bộ passengerId
        List<UUID> passengerIds = stops.stream()
                .flatMap(stop -> stop.getStopJourneyMappings().stream())
                .map(m -> m.getPassengerJourney().getPassengerId())
                .distinct()
                .toList();

        // 2. Lấy 1 lần tất cả Passenger theo danh sách id
        Map<UUID, PassengerDTO> passengerMap = passengerService
                .getPassengersByIds(passengerIds)
                .stream()
                .collect(Collectors.toMap(PassengerDTO::id, p -> p));

        // 3. Dựng StopDTO + PassengerOnStopDTO
        return  stops.stream()
                .map(stop -> {
                    List<PassengerOnStopDTO> passengerOnStopDTOS = stop.getStopJourneyMappings().stream()
                            .map(stopJourneyMapping -> {
                                PassengerDTO passenger = passengerMap.get(
                                        stopJourneyMapping.getPassengerJourney().getPassengerId()
                                );
                                PassengerJourneyInfo passengerJourneyInfo = new PassengerJourneyInfo(
                                        passenger.id(),
                                        passenger.account().firstName(),
                                        passenger.account().lastName(),
                                        passenger.account().phoneNumber(),
                                        passenger.account().email(),
                                        stopJourneyMapping.getPassengerJourney().getSeatCount()
                                );
                                return new PassengerOnStopDTO(
                                        passengerJourneyInfo,
                                        stopJourneyMapping.getAction()
                                );
                            }).toList();
                    return new StopDTO(
                            stop.getId(),
                            stop.getLatitude(),
                            stop.getLongitude(),
                            stop.getSequence(),
                            stop.getPlannedDepartureTime(),
                            stop.getActualDepartureTime(),
                            passengerOnStopDTOS
                    );
                }).toList();
    }

}

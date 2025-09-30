package vn.edu.fpt.transitlink.mapbox_integration.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.mapbox_integration.PolylineMerger;
import vn.edu.fpt.transitlink.mapbox_integration.client.direction.DirectionClient;
import vn.edu.fpt.transitlink.mapbox_integration.client.direction.dto.response.*;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.OptimizationClient;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.OptimizationMapper;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request.OptimizationRequestBody;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response.OptimizationRoute;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response.RetrieveResponse;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response.Stop;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response.SubmissionResponse;
import vn.edu.fpt.transitlink.shared.exception.SystemException;
import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;
import vn.edu.fpt.transitlink.trip.dto.*;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;
import vn.edu.fpt.transitlink.trip.enumeration.StopAction;
import vn.edu.fpt.transitlink.trip.spi.RouteOptimizationProvider;
import vn.edu.fpt.transitlink.trip.spi.dto.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class MapboxRouteOptimizationProvider implements RouteOptimizationProvider {

    private final OptimizationClient optimizationClient;
    private final DirectionClient directionClient;
    private static final DateTimeFormatter MAPBOX_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public OptimizationResult optimizeRoutes(List<PassengerJourneyDTO> passengerJourneys,
                                             RouteType routeType,
                                             List<VehicleDTO> vehicles) {

        String jobId = submitOptimization(passengerJourneys, vehicles);

        RetrieveResponse retrieveResponse = retrieveSolution(jobId);

        OptimizationResult result = mapResult(retrieveResponse, passengerJourneys, routeType);

        List<DirectionResponse> directions = result.getRoutes().stream()
                .map(this::fetchDirections)
                .toList();

        enrichRoutes(result, directions);

        return result;
    }

    private String submitOptimization(List<PassengerJourneyDTO> passengerJourneys,
                                      List<VehicleDTO> vehicles) {
        try {
            OptimizationRequestBody body = OptimizationMapper.buildRoutingProblem(passengerJourneys, vehicles);
            ResponseEntity<SubmissionResponse> response = optimizationClient.submitRoutingProblem(body);
            return response.getBody().id();
        } catch (RestClientResponseException e) {
            throw new ThirdPartyException("Failed to submit routing optimizationRequestBody",
                    "Optimization v2 service",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),
                    e);
        } catch (RuntimeException e) {
            throw new SystemException("Failed to submit routing optimizationRequestBody", e);
        }
    }

    private RetrieveResponse retrieveSolution(String jobId) {
        try {
            Thread.sleep(2000); // wait before first poll
            for (int i = 0; i < 5; i++) {
                ResponseEntity<RetrieveResponse> response = optimizationClient.retrieveSolution(jobId);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                }
                Thread.sleep(2000);
            }
            throw new SystemException("Failed to retrieve routing solution after max retries");
        } catch (RestClientResponseException e) {
            throw new ThirdPartyException("Failed to retrieve routing solution",
                    "Optimization v2 service",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),
                    e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemException("Thread was interrupted", e);
        } catch (RuntimeException e) {
            throw new SystemException("Failed to retrieve routing solution", e);
        }
    }

    private OptimizationResult mapResult(RetrieveResponse response, List<PassengerJourneyDTO> passengers, RouteType routeType) {
        List<OptimizationRoute> optimizationRoutes = response.routes();

// khởi tạo PassengerJourneyData từ danh sách hành khách gốc
        Map<UUID, PassengerJourneyData> passengerJourneyById = passengers.stream()
                .map(passenger -> {
                    PassengerJourneyData journey = new PassengerJourneyData();
                    journey.setPassengerJourneyId(passenger.getId());
                    return journey;
                })
                .collect(Collectors.toMap(
                        PassengerJourneyData::getPassengerJourneyId,
                        journey -> journey
                ));

// cập nhật plannedDepartureTime & plannedArrivalTime cho từng hành khách dựa trên stops
        optimizationRoutes.stream()
                .flatMap(route -> route.stops().stream())
                .forEach(stop -> {
                    Instant estimatedTime = stop.eta();

                    // pickups → departure
                    if (stop.pickups() != null) {
                        stop.pickups().forEach(passengerIdStr -> {
                            UUID passengerId = UUID.fromString(passengerIdStr);
                            Optional.ofNullable(passengerJourneyById.get(passengerId))
                                    .ifPresent(journey ->
                                            journey.setPlannedDepartureTime(estimatedTime.atOffset(ZoneOffset.UTC))
                                    );
                        });
                    }

                    // dropoffs → arrival
                    if (stop.dropoffs() != null) {
                        stop.dropoffs().forEach(passengerIdStr -> {
                            UUID passengerId = UUID.fromString(passengerIdStr);
                            Optional.ofNullable(passengerJourneyById.get(passengerId))
                                    .ifPresent(journey ->
                                            journey.setPlannedArrivalTime(estimatedTime.atOffset(ZoneOffset.UTC))
                                    );
                        });
                    }
                });

        List<PassengerJourneyData> enrichedPassengerJourneys =
                new ArrayList<>(passengerJourneyById.values());


        List<UUID> droppedPassengerIds = response.dropped().shipments().stream()
                .map(UUID::fromString)
                .toList();

        List<RouteData> routes = optimizationRoutes.stream()
                .map(route -> {
                    List<StopData> stops = IntStream.range(0, route.stops().size())
                            .mapToObj(i -> {
                                Stop stop = route.stops().get(i);
                                List<String> pickups = stop.pickups();
                                List<String> dropoffs = stop.dropoffs();
                                List<PassengerOnStopData> passengerOnStops = new ArrayList<>();
                                if (pickups != null) {
                                    pickups.forEach(pickup -> {
                                        passengerOnStops.add(new PassengerOnStopData(
                                                UUID.fromString(pickup),
                                                StopAction.PICKUP)
                                        );
                                    });
                                }
                                if (dropoffs != null) {
                                    dropoffs.forEach(dropoff -> {
                                        passengerOnStops.add(new PassengerOnStopData(
                                                UUID.fromString(dropoff),
                                                StopAction.DROPOFF
                                        ));
                                    });
                                }

                                return new StopData(
                                        stop.locationMetadata().snappedCoordinate().isEmpty()
                                                ? 0.0 : stop.locationMetadata().snappedCoordinate().getLast(),
                                        stop.locationMetadata().snappedCoordinate().isEmpty()
                                                ? 0.0 : stop.locationMetadata().snappedCoordinate().get(0),
                                        i,
                                        stop.eta().atOffset(ZoneOffset.UTC),
                                        passengerOnStops
                                );

                            })
                            .toList();

                    List<String> waypointAddresses = stops.stream()
                            .map(
                                    stop -> {
                                        String lat = String.valueOf(stop.getLatitude());
                                        String lon = String.valueOf(stop.getLongitude());
                                        return lat + "," + lon;
                                    }
                            )
                            .toList();
                    String googleMapUrl = buildRouteUrl(
                            waypointAddresses.isEmpty() ? null : waypointAddresses.get(0),
                            waypointAddresses.size() < 2 ? null : waypointAddresses.get(waypointAddresses.size() - 1),
                            waypointAddresses.size() <= 2 ? null : waypointAddresses.subList(1, waypointAddresses.size() - 1)
                    );

                    RouteData routeData = new RouteData();
                    routeData.setType(routeType);
                    routeData.setVehicleId(UUID.fromString(route.vehicle()));
                    routeData.setStops(stops);
                    routeData.setDirectionUrl(googleMapUrl);
                    return routeData;
                })
                .toList();

        return new OptimizationResult(droppedPassengerIds, routes, enrichedPassengerJourneys);
    }

    private DirectionResponse fetchDirections(RouteData route) {
        String coordinates = route.getStops().stream()
                .map(stop -> stop.getLongitude() + "," + stop.getLatitude())
                .collect(Collectors.joining(";"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("coordinates", coordinates);
        body.add("steps", "true");
        body.add("geometries", "polyline");
        body.add("overview", "full");
        body.add("language", "vi");
        body.add("roundabout_exits", "true");
        body.add("continue_straight", "true");
        body.add("exclude", "toll,ferry");
        body.add("waypoints_per_route", "true");
        try {
            ResponseEntity<DirectionResponse> response = directionClient.getDirections(body);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new ThirdPartyException("Failed to get directions from Mapbox Directions API",
                        "Directions API",
                        response.getStatusCode().value(),
                        String.valueOf(response.getBody()));
            }
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ThirdPartyException("Failed to get directions from Mapbox Directions API",
                    "Directions API",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),
                    e);
        } catch (RuntimeException e) {
            throw new SystemException("Failed to get directions from Mapbox Directions API", e);
        }
    }

    private void enrichRoutes(OptimizationResult result, List<DirectionResponse> directions) {

        List<RouteData> routes = result.getRoutes();
        List<PassengerJourneyData> passengerJourneys = result.getPassengerJourneys();

        Map<UUID, PassengerJourneyData> passengerJourneyById = passengerJourneys.stream()
                .collect(Collectors.toMap(
                        PassengerJourneyData::getPassengerJourneyId,
                        Function.identity()
                ));

        // list of leg index to leg geometry map for each route
        List<Map<Integer, String>> legGeometryMap = directions.stream()
                .map(directionResponse -> {
                    if (directionResponse.routes().isEmpty()) {
                        return Collections.<Integer, String>emptyMap();
                    }

                    List<String> legPolylines = directionResponse.routes()
                            .get(0)
                            .legs()
                            .stream()
                            .map(leg -> {
                                List<String> stepPolylines = leg.steps().stream()
                                        .map(Step::geometry)
                                        .toList();
                                return PolylineMerger.mergePolylines(stepPolylines);
                            })
                            .toList();

                    return IntStream.range(0, legPolylines.size())
                            .boxed()
                            .collect(Collectors.toMap(
                                    i -> i,
                                    legPolylines::get,
                                    (a, b) -> a,
                                    LinkedHashMap::new
                            ));
                })
                .toList();


        IntStream.range(0, routes.size()).forEach(i -> {
            RouteData route = routes.get(i);
            Map<Integer, String> legMap = legGeometryMap.get(i);
            DirectionRoute dir = directions.get(i).routes().isEmpty()
                    ? null
                    : directions.get(i).routes().get(0);

            if (dir == null || route.getStops().isEmpty()) {
                return; // bỏ qua nếu không có dữ liệu
            }

            route.setGeometry(dir.geometry());
            route.setEstimatedDurationMin(dir.duration() / 60);
            route.setEstimatedDistanceKm(dir.distance() / 1000);

            route.setPlannedDepartureTime(route.getStops().get(0).getPlannedDepartureTime());
            route.setPlannedArrivalTime(route.getStops().get(route.getStops().size() - 1).getPlannedDepartureTime());


            // enrich passenger journey data
            route.getStops().forEach(stop -> {
                int startCuttingPoint = stop.getSequence();

                List<String> subLegPolylines = legMap.entrySet().stream()
                        .filter(e -> e.getKey() >= startCuttingPoint)
                        .map(Map.Entry::getValue)
                        .toList();

                String passengerJourneyGeometry = PolylineMerger.mergePolylines(subLegPolylines);

                stop.getPassengerOnStopDatas().forEach(passengerOnStopData -> {
                    if (passengerOnStopData.getAction() == StopAction.DROPOFF) {
                        return; // skip nếu là dropoff
                    }
                    UUID passengerJourneyId = passengerOnStopData.getPassengerJourneyId();
                    PassengerJourneyData journey = passengerJourneyById.get(passengerJourneyId);
                    if (journey != null) {
                        journey.setGeometry(passengerJourneyGeometry);
                    }
                });
            });

        });
    }


    public String buildRouteUrl(String origin, String destination, List<String> waypoints) {
        StringBuilder url = new StringBuilder("https://www.google.com/maps/dir/?api=1");

        try {
            if (origin != null && !origin.isEmpty()) {
                url.append("&origin=").append(URLEncoder.encode(origin, "UTF-8"));
            }

            if (destination != null && !destination.isEmpty()) {
                url.append("&destination=").append(URLEncoder.encode(destination, "UTF-8"));
            }

            if (waypoints != null && !waypoints.isEmpty()) {
                url.append("&waypoints=");
                for (int i = 0; i < waypoints.size(); i++) {
                    url.append(URLEncoder.encode(waypoints.get(i), "UTF-8"));
                    if (i < waypoints.size() - 1) {
                        url.append("|");
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url.toString();
    }
}



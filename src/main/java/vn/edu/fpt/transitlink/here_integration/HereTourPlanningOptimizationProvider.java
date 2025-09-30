//package vn.edu.fpt.transitlink.here_integration;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
//import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
//import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
//import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
//import vn.edu.fpt.transitlink.trip.dto.RouteDTO;
//import vn.edu.fpt.transitlink.trip.dto.StopDTO;
//
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
///**
// * Provider tích hợp với HERE Tour Planning Optimization API
// * để build problem từ domain model và gửi request tối ưu hóa.
// */
//public class HereTourPlanningOptimizationProvider {
//
//    private final String apiKey = "xxxxxx";
//
//    public RouteDTO optimize(List<StopDTO> stops) {
//        String endpointUrl = "https://tourplanning.hereapi.com/v3/problems?apikey=" + apiKey;
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        Map<String, Object> problemRequestBody = new HashMap<>();
//
//        HttpEntity<Map<String, Object>> httpRequest =
//                new HttpEntity<>(problemRequestBody, requestHeaders);
//
//        ResponseEntity<String> httpResponse =
//                restTemplate.postForEntity(endpointUrl, httpRequest, String.class);
//
//        // TODO: parse httpResponse.getBody() -> RouteDTO
//        return null;
//    }
//
//    public static Map<String, Object> buildProblemFromDomain(
//            List<VehicleDTO> vehicles,
//            List<DepotDTO> depots,
//            Map<UUID, PlaceDTO> placeById,
//            List<PassengerJourneyDTO> journeys,
//            OffsetDateTime shiftStart,
//            OffsetDateTime shiftEnd
//    ) {
//        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
//                .withZone(ZoneOffset.UTC);;
//
//        Map<UUID, DepotDTO> depotIndex = new HashMap<>();
//        for (DepotDTO depot : depots) {
//            depotIndex.put(depot.id(), depot);
//        }
//
//        Map<String, Object> problemDefinition = new LinkedHashMap<>();
//
//        List<Map<String, Object>> groupDefinitions = new ArrayList<>();
//
//
//
//
//
//        List<Map<String, Object>> jobDefinitions = new ArrayList<>();
//        for (PassengerJourneyDTO journey : journeys) {
//            PlaceDTO pickupLocation = placeById.get(journey.pickupPlace().id());
//            PlaceDTO dropoffLocation = placeById.get(journey.dropoffPlace().id());
//
//            if (pickupLocation == null || dropoffLocation == null) {
//                throw new IllegalArgumentException("Journey " + journey.id() +
//                        " missing pickup/dropoff place");
//            }
//
//            int passengerDemand = Optional.ofNullable(journey.seatCount()).orElse(1);
//
//            // Tính timeWindows nếu có latestStopArrivalTime
//            List<List<String>> timeWindows = null;
//            if (journey.lastestStopArrivalTime() != null) {
//                OffsetDateTime latestArrival = journey.lastestStopArrivalTime();
//                timeWindows = List.of(List.of(
//                        isoFormatter.format(latestArrival.minusMinutes(5)),
//                        isoFormatter.format(latestArrival.plusMinutes(5))
//                ));
//            }
//
//            Map<String, Object> pickupTask = mapOf(
//                    "places", List.of(mapOf(
//                            "location", mapOf(
//                                    "lat", pickupLocation.latitude(),
//                                    "lng", pickupLocation.longitude()
//                            ),
//                            "duration", 60
//                    )),
//                    "demand", List.of(passengerDemand)
//            );
//
//            Map<String, Object> dropoffTask = mapOf(
//                    "places", List.of(mapOf(
//                            "location", mapOf(
//                                    "lat", dropoffLocation.latitude(),
//                                    "lng", dropoffLocation.longitude()
//                            ),
//                            "duration", 60
//                    )),
//                    "demand", List.of(passengerDemand)
//            );
//
//            // Gắn timeWindows đúng theo journeyType
//            if (timeWindows != null) {
//                switch (journey.journeyType()) {
//                    case POST_TRIP -> ((Map<String, Object>) ((List<?>) pickupTask.get("places")).get(0))
//                            .put("times", timeWindows);
//                    case PRE_TRIP -> ((Map<String, Object>) ((List<?>) dropoffTask.get("places")).get(0))
//                            .put("times", timeWindows);
//                    default -> {
//                    }
//                }
//            }
//
//            // Dọn null values trong dropoffTask (nếu có)
//            ((Map<String, Object>) ((List<?>) dropoffTask.get("places")).get(0))
//                    .values().removeIf(Objects::isNull);
//
//            Map<String, Object> jobTasks = new LinkedHashMap<>();
//            jobTasks.put("pickups", List.of(pickupTask));
//            jobTasks.put("deliveries", List.of(dropoffTask));
//
//            String jobIdentifier = "passenger-" + journey.passenger().id()
//                    + "-" + journey.id();
//
//            Map<String, Object> jobDefinition = new LinkedHashMap<>();
//            jobDefinition.put("id", jobIdentifier);
//            jobDefinition.put("tasks", jobTasks);
//
//            jobDefinitions.add(jobDefinition);
//
//
//            Map<String, Object> fleetDefinition = new LinkedHashMap<>();
//            fleetDefinition.put("profiles", List.of(mapOf("name", "car", "type", "car")));
//
//            List<Map<String, Object>> vehicleTypes = new ArrayList<>();
//            for (VehicleDTO vehicle : vehicles) {
//                DepotDTO assignedDepot = depotIndex.get(vehicle.depot().id());
//                if (assignedDepot == null) {
//                    throw new IllegalArgumentException("Vehicle " + vehicle.id() +
//                            " missing depot: " + vehicle.depot().id());
//                }
//
//                PlaceDTO depotLocation = placeById.get(assignedDepot.place().id());
//                if (depotLocation == null) {
//                    throw new IllegalArgumentException("Depot " + assignedDepot.id() +
//                            " missing place: " + assignedDepot.place().id());
//                }
//
//                Map<String, Object> vehicleType = new LinkedHashMap<>();
//                vehicleType.put("id", vehicle.id());
//                vehicleType.put("profile", "car");
//                vehicleType.put("capacity", List.of(Optional.ofNullable(vehicle.capacity()).orElse(1)));
//                vehicleType.put("vehicleIds", List.of(vehicle.licensePlate()));
//                vehicleType.put("costs", mapOf("time", 1.0));
//
//                Map<String, Object> vehicleShift = mapOf(
//                        "start", mapOf(
//                                "time", isoFormatter.format(shiftStart),
//                                "location", mapOf(
//                                        "lat", depotLocation.latitude(),
//                                        "lng", depotLocation.longitude()
//                                )
//                        ),
//                        "end", mapOf(
//                                "time", isoFormatter.format(shiftEnd),
//                                "location", mapOf(
//                                        "lat", depotLocation.latitude(),
//                                        "lng", depotLocation.longitude()
//                                )
//                        )
//                );
//                vehicleType.put("shifts", List.of(vehicleShift));
//
//                vehicleTypes.add(vehicleType);
//            }
//            fleetDefinition.put("types", vehicleTypes);
//            problemDefinition.put("fleet", fleetDefinition);
//        }
//
//        Map<String, Object> planDefinition = new LinkedHashMap<>();
//        planDefinition.put("jobs", jobDefinitions);
//        problemDefinition.put("plan", planDefinition);
//
//        return problemDefinition;
//    }
//
//    public static Map<String, Object> mapOf(Object... keyValues) {
//        Map<String, Object> map = new LinkedHashMap<>();
//        for (int i = 0; i < keyValues.length; i += 2) {
//            map.put((String) keyValues[i], keyValues[i + 1]);
//        }
//        return map;
//    }
//
//// build group definition
//    private Map<String, Object> buildGroupDefinition(
//            List<Map<String, Object>> pudoList
//    ) {
//        return mapOf(
//                "groups", mapOf(
//                        "id", UUID.randomUUID().toString(),
//                        "placement", "strict",
//                        "pudos", pudoList
//                )
//        );
//    }
//
//    private Map<String, Object> buildPudo(
//            String pudoType,
//            int placeDuration,
//            List<String> placeTimes,
//            double lat,
//            double lng
//    ) {
//        return mapOf(
//                "id", UUID.randomUUID().toString(),
//                "assignAt", pudoType,
//                "places", mapOf(
//                        "duration", placeDuration,
//                        "times", placeTimes,
//                        "location", mapOf(
//                                "lat", lat,
//                                "lng", lng
//                        )
//                )
//        );
//    }
//
//    // build job definition
//    private Map<String, Object> buildJobDefinition(
//            boolean isPickup
//
//    ) {
//        if (isPickup) {
//            return mapOf(
//                    "id", UUID.randomUUID().toString(),
//                    "tasks", mapOf(
//                            "pickups", mapOf(
//                                    "demand", List.of(1)
//                            )
//                    )
//            );
//        } else {
//            return mapOf(
//                    "id", UUID.randomUUID().toString(),
//                    "tasks", mapOf(
//                            "deliveries", dropoffList
//                    )
//            );
//        }
//    }
//
//
//
//}

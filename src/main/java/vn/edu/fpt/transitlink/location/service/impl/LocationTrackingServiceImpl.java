package vn.edu.fpt.transitlink.location.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.location.dto.DriverLocationMessage;
import vn.edu.fpt.transitlink.location.dto.PassengerLocationMessage;
import vn.edu.fpt.transitlink.location.entity.DriverLocation;
import vn.edu.fpt.transitlink.location.entity.PassengerLocation;
import vn.edu.fpt.transitlink.location.mapper.DriverLocationMapper;
import vn.edu.fpt.transitlink.location.mapper.PassengerLocationMapper;
import vn.edu.fpt.transitlink.location.repository.DriverLocationRepository;
import vn.edu.fpt.transitlink.location.repository.PassengerLocationRepository;
import vn.edu.fpt.transitlink.location.service.LocationTrackingService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LocationTrackingServiceImpl implements LocationTrackingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final DriverLocationRepository driverLocationRepository;
    private final PassengerLocationRepository passengerLocationRepository;
    private final PassengerLocationMapper passengerLocationMapper;
    private final DriverLocationMapper driverLocationMapper;
    private final ObjectMapper objectMapper;

    private static final int DISTANCE_THRESHOLD = 10; // meters
    private static final int TIME_THRESHOLD = 5; // seconds

    @Override
    public void updateDriverLocation(DriverLocationMessage message) {
        DriverLocation newLoc = buildDriverLocation(message);

        DriverLocation lastLoc = getLastDriverLocation(newLoc.getDriverId());

        if (isSignificantLocation(newLoc, lastLoc)) {
            saveDriverLocation(newLoc);
        }

        messagingTemplate.convertAndSend("/topic/driver/" + newLoc.getDriverId(),
                driverLocationMapper.toDTO(newLoc));
    }

    @Override
    public void updatePassengerLocation(PassengerLocationMessage message) {
        PassengerLocation newLoc = buildPassengerLocation(message);

        PassengerLocation lastLoc = getLastPassengerLocation(newLoc.getPassengerId());

        if (isSignificantPassengerLocation(newLoc, lastLoc)) {
            savePassengerLocation(newLoc);
        }

        messagingTemplate.convertAndSend("/topic/passenger/" + newLoc.getPassengerId(),
                passengerLocationMapper.toDTO(newLoc));
    }

    private DriverLocation buildDriverLocation(DriverLocationMessage message) {
        DriverLocation loc = new DriverLocation();
        loc.setDriverId(message.driverId());
        loc.setLatitude(message.latitude());
        loc.setLongitude(message.longitude());
        loc.setRecordAt(OffsetDateTime.now());
        return loc;
    }

    private PassengerLocation buildPassengerLocation(PassengerLocationMessage message) {
        PassengerLocation loc = new PassengerLocation();
        loc.setPassengerId(message.passengerId());
        loc.setLatitude(message.latitude());
        loc.setLongitude(message.longitude());
        loc.setRecordAt(OffsetDateTime.now());
        return loc;
    }

    private DriverLocation getLastDriverLocation(UUID driverId) {
        String json = (String) redisTemplate.opsForHash().get("driver:lastLocations", driverId.toString());
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, DriverLocation.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private PassengerLocation getLastPassengerLocation(UUID passengerId) {
        String json = (String) redisTemplate.opsForHash().get("passenger:lastLocations", passengerId.toString());
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, PassengerLocation.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void saveDriverLocation(DriverLocation loc) {
        try {
            String json = objectMapper.writeValueAsString(loc);

            // Lưu vị trí mới nhất vào Hash
            redisTemplate.opsForHash().put("driver:lastLocations", loc.getDriverId().toString(), json);

            // Lưu lịch sử vị trí vào Sorted Set
            redisTemplate.opsForZSet().add("driver:" + loc.getDriverId() + ":locations",
                    json,
                    loc.getRecordAt().toInstant().getEpochSecond());

        } catch (JsonProcessingException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

    private void savePassengerLocation(PassengerLocation loc) {
        try {
            String json = objectMapper.writeValueAsString(loc);

            redisTemplate.opsForHash().put("passenger:lastLocations", loc.getPassengerId().toString(), json);

            redisTemplate.opsForZSet().add("passenger:" + loc.getPassengerId() + ":locations",
                    json,
                    loc.getRecordAt().toInstant().getEpochSecond());

        } catch (JsonProcessingException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

    private boolean isSignificantLocation(DriverLocation newLoc, DriverLocation lastLoc) {
        if (lastLoc == null) return true;
        Point p1 = Point.fromLngLat(lastLoc.getLongitude(), lastLoc.getLatitude());
        Point p2 = Point.fromLngLat(newLoc.getLongitude(), newLoc.getLatitude());
        double distance = TurfMeasurement.distance(p1, p2, "meters");
        long timeDiff = Duration.between(lastLoc.getRecordAt(), newLoc.getRecordAt()).getSeconds();
        return distance >= DISTANCE_THRESHOLD || timeDiff >= TIME_THRESHOLD;
    }

    private boolean isSignificantPassengerLocation(PassengerLocation newLoc, PassengerLocation lastLoc) {
        if (lastLoc == null) return true;
        Point p1 = Point.fromLngLat(lastLoc.getLongitude(), lastLoc.getLatitude());
        Point p2 = Point.fromLngLat(newLoc.getLongitude(), newLoc.getLatitude());
        double distance = TurfMeasurement.distance(p1, p2, "meters");
        long timeDiff = Duration.between(lastLoc.getRecordAt(), newLoc.getRecordAt()).getSeconds();
        return distance >= DISTANCE_THRESHOLD || timeDiff >= TIME_THRESHOLD;
    }

    @Scheduled(fixedRate = 60_000) // mỗi phút chạy
    public void flushBatches() {
        // driver locations
        Set<String> driverKeys = redisTemplate.keys("driver:*:locations");
        if (driverKeys != null) {
            driverKeys.forEach(key -> {
                Set<Object> batch = redisTemplate.opsForZSet().range(key, 0, -1);
                if (batch != null && !batch.isEmpty()) {
                    driverLocationRepository.saveAll(batch.stream()
                            .map(o -> {
                                try {
                                    return objectMapper.readValue((String) o, DriverLocation.class);
                                } catch (JsonProcessingException e) {
                                    return null;
                                }
                            })
                            .filter(loc -> loc != null)
                            .toList());
                    redisTemplate.delete(key);
                }
            });
        }

        // passenger locations
        Set<String> passengerKeys = redisTemplate.keys("passenger:*:locations");
        if (passengerKeys != null) {
            passengerKeys.forEach(key -> {
                Set<Object> batch = redisTemplate.opsForZSet().range(key, 0, -1);
                if (batch != null && !batch.isEmpty()) {
                    passengerLocationRepository.saveAll(batch.stream()
                            .map(o -> {
                                try {
                                    return objectMapper.readValue((String) o, PassengerLocation.class);
                                } catch (JsonProcessingException e) {
                                    return null;
                                }
                            })
                            .filter(loc -> loc != null)
                            .toList());
                    redisTemplate.delete(key);
                }
            });
        }
    }
}

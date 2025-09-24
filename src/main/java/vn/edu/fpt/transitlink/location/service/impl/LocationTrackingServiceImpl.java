package vn.edu.fpt.transitlink.location.service.impl;

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
import vn.edu.fpt.transitlink.location.repository.DriverLocationRepository;
import vn.edu.fpt.transitlink.location.repository.PassengerLocationRepository;
import vn.edu.fpt.transitlink.location.service.LocationTrackingService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationTrackingServiceImpl implements LocationTrackingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final DriverLocationRepository driverLocationRepository;
    private final PassengerLocationRepository passengerLocationRepository;

    private static final int BATCH_SIZE = 50; // batch size
    private static final int DISTANCE_THRESHOLD = 10; // meters
    private static final int TIME_THRESHOLD = 5; // seconds

    @Override
    public void updateDriverLocation(DriverLocationMessage message) {
        DriverLocation newLoc = new DriverLocation();
        newLoc.setDriverId(message.driverId());
        newLoc.setLatitude(message.latitude());
        newLoc.setLongitude(message.longitude());
        newLoc.setRecordAt(OffsetDateTime.now());

        String listKey = "driver:" + newLoc.getDriverId() + ":locations";

        DriverLocation lastLoc = (DriverLocation) redisTemplate.opsForValue().get("driver:" + newLoc.getDriverId() + ":lastLocation");

        if (isSignificantLocation(newLoc, lastLoc)) {
            redisTemplate.opsForList().rightPush(listKey, newLoc);
            redisTemplate.opsForValue().set("driver:" + newLoc.getDriverId() + ":lastLocation", newLoc);
        }

        // gửi realtime
        messagingTemplate.convertAndSend("/topic/driver/" + newLoc.getDriverId(), newLoc);
    }

    @Override
    public void updatePassengerLocation(PassengerLocationMessage message) {
        PassengerLocation newLoc = new PassengerLocation();
        newLoc.setPassengerId(message.passengerId());
        newLoc.setLatitude(message.latitude());
        newLoc.setLongitude(message.longitude());
        newLoc.setRecordAt(OffsetDateTime.now());

        String listKey = "passenger:" + newLoc.getPassengerId() + ":locations";

        PassengerLocation lastLoc = (PassengerLocation) redisTemplate.opsForValue().get("passenger:" + newLoc.getPassengerId() + ":lastLocation");

        if (isSignificantPassengerLocation(newLoc, lastLoc)) {
            redisTemplate.opsForList().rightPush(listKey, newLoc);
            redisTemplate.opsForValue().set("passenger:" + newLoc.getPassengerId() + ":lastLocation", newLoc);
        }

        // gửi realtime
        messagingTemplate.convertAndSend("/topic/passenger/" + newLoc.getPassengerId(), newLoc);
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
        redisTemplate.keys("driver:*:locations").forEach(key -> {
            List<Object> batch = redisTemplate.opsForList().range(key, 0, -1);
            if (batch != null && !batch.isEmpty()) {
                driverLocationRepository.saveAll(batch.stream()
                        .map(o -> (DriverLocation) o)
                        .toList());
                redisTemplate.delete(key);
            }
        });

        // passenger locations
        redisTemplate.keys("passenger:*:locations").forEach(key -> {
            List<Object> batch = redisTemplate.opsForList().range(key, 0, -1);
            if (batch != null && !batch.isEmpty()) {
                passengerLocationRepository.saveAll(batch.stream()
                        .map(o -> (PassengerLocation) o)
                        .toList());
                redisTemplate.delete(key);
            }
        });
    }
}

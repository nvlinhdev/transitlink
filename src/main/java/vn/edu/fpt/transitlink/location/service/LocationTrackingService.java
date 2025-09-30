package vn.edu.fpt.transitlink.location.service;

import vn.edu.fpt.transitlink.location.dto.DriverLocationMessage;
import vn.edu.fpt.transitlink.location.dto.PassengerLocationMessage;

public interface LocationTrackingService {
    void updateDriverLocation(DriverLocationMessage message);
    void updatePassengerLocation(PassengerLocationMessage message);
}

package vn.edu.fpt.transitlink.trip.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class StopData {
    private Double latitude;
    private Double longitude;
    private Integer sequence;
    private OffsetDateTime plannedDepartureTime;
    private List<PassengerOnStopData> passengerOnStopDatas;
}

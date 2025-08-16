package vn.edu.fpt.transitlink.schedule.dto;

import java.util.Date;
import java.util.UUID;

public record ScheduleDTO() {
     UUID id;
     String routeId;
     Date date;
}

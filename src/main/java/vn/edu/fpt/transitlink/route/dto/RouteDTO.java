package vn.edu.fpt.transitlink.route.dto;

import java.util.UUID;

public record RouteDTO() {
     UUID id;
     String name;
     String startLocation;
     String endLocation;
     String waypoints;
}


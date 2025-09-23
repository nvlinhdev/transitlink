package vn.edu.fpt.transitlink.trip.spi;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.enumeration.RouteType;
import vn.edu.fpt.transitlink.trip.spi.dto.OptimizationResult;

import java.util.List;

public interface RouteOptimizationProvider {
    OptimizationResult optimizeRoutes(List<PassengerJourneyDTO> passengerJourneys, RouteType routeType, List<VehicleDTO> vehicles);
}

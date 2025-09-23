package vn.edu.fpt.transitlink.fleet.service;

import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.request.CreateVehicleRequest;
import vn.edu.fpt.transitlink.fleet.request.UpdateVehicleRequest;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    VehicleDTO createVehicle(CreateVehicleRequest request);
    VehicleDTO updateVehicle(UUID id, UpdateVehicleRequest request);
    VehicleDTO deleteVehicle(UUID id, UUID deletedBy);
    VehicleDTO restoreVehicle(UUID id);
    void hardDeleteVehicle(UUID id);
    VehicleDTO getVehicle(UUID id);
    List<VehicleDTO> getVehicles(int page, int size);
    long countVehicles();
    List<VehicleDTO> getDeletedVehicles(int page, int size);
    long countDeletedVehicles();
    List<VehicleDTO> getAllVehiclesByIds(List<UUID> ids);
}

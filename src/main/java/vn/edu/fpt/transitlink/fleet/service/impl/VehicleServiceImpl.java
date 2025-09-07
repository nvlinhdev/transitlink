package vn.edu.fpt.transitlink.fleet.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
import vn.edu.fpt.transitlink.fleet.dto.VehicleDTO;
import vn.edu.fpt.transitlink.fleet.entity.Vehicle;
import vn.edu.fpt.transitlink.fleet.exception.FleetErrorCode;
import vn.edu.fpt.transitlink.fleet.repository.VehicleRepository;
import vn.edu.fpt.transitlink.fleet.request.CreateVehicleRequest;
import vn.edu.fpt.transitlink.fleet.request.UpdateVehicleRequest;
import vn.edu.fpt.transitlink.fleet.service.DepotService;
import vn.edu.fpt.transitlink.fleet.service.VehicleService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final DepotService depotService;

    @Transactional
    @Caching(
            put = {@CachePut(value = "vehiclesById", key = "#result.id()")},
            evict = {@CacheEvict(value = "vehiclesPage", allEntries = true)}
    )
    @Override
    public VehicleDTO createVehicle(CreateVehicleRequest request) {
        // Verify depot exists
        DepotDTO depotDTO = depotService.getDepot(request.depotId());
        if (depotDTO == null) {
            throw new BusinessException(FleetErrorCode.VEHICLE_DEPOT_NOT_FOUND);
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setName(request.name());
        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setCapacity(request.capacity());
        vehicle.setFuelType(request.fuelType());
        vehicle.setFuelConsumptionRate(request.fuelConsumptionRate());
        vehicle.setDepotId(request.depotId());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return new VehicleDTO(
                savedVehicle.getId(),
                savedVehicle.getName(),
                savedVehicle.getLicensePlate(),
                savedVehicle.getFuelType(),
                savedVehicle.getCapacity(),
                savedVehicle.getFuelConsumptionRate(),
                depotDTO
        );
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "vehiclesById", key = "#id")},
            evict = {@CacheEvict(value = "vehiclesPage", allEntries = true)}
    )
    @Override
    public VehicleDTO updateVehicle(UUID id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.VEHICLE_NOT_FOUND));

        // Get depot data
        DepotDTO depotDTO;
        if (request.depotId() != null) {
            depotDTO = depotService.getDepot(request.depotId());
            if (depotDTO == null) {
                throw new BusinessException(FleetErrorCode.VEHICLE_DEPOT_NOT_FOUND);
            }
            vehicle.setDepotId(request.depotId());
        } else {
            depotDTO = depotService.getDepot(vehicle.getDepotId());
            if (depotDTO == null) {
                throw new BusinessException(FleetErrorCode.VEHICLE_DEPOT_NOT_FOUND);
            }
        }

        if (request.name() != null) {
            vehicle.setName(request.name());
        }
        if (request.licensePlate() != null) {
            vehicle.setLicensePlate(request.licensePlate());
        }
        if (request.capacity() != null) {
            vehicle.setCapacity(request.capacity());
        }
        if (request.fuelType() != null) {
            vehicle.setFuelType(request.fuelType());
        }
        if (request.fuelConsumptionRate() != null) {
            vehicle.setFuelConsumptionRate(request.fuelConsumptionRate());
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return new VehicleDTO(
                savedVehicle.getId(),
                savedVehicle.getName(),
                savedVehicle.getLicensePlate(),
                savedVehicle.getFuelType(),
                savedVehicle.getCapacity(),
                savedVehicle.getFuelConsumptionRate(),
                depotDTO
        );
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "vehiclesById", key = "#id"),
                    @CacheEvict(value = "vehiclesPage", allEntries = true),
                    @CacheEvict(value = "deletedVehiclesPage", allEntries = true)
            }
    )
    @Override
    public VehicleDTO deleteVehicle(UUID id, UUID deletedBy) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.VEHICLE_NOT_FOUND));

        if (vehicle.isDeleted()) {
            throw new BusinessException(FleetErrorCode.VEHICLE_ALREADY_DELETED);
        }

        // Get depot data before deleting the vehicle
        DepotDTO depotDTO = depotService.getDepot(vehicle.getDepotId());
        if (depotDTO == null) {
            throw new BusinessException(FleetErrorCode.VEHICLE_DEPOT_NOT_FOUND);
        }

        vehicle.softDelete(deletedBy);
        vehicleRepository.save(vehicle);

        // Return vehicle data for potential restoration
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getLicensePlate(),
                vehicle.getFuelType(),
                vehicle.getCapacity(),
                vehicle.getFuelConsumptionRate(),
                depotDTO
        );
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "vehiclesById", key = "#id")},
            evict = {
                    @CacheEvict(value = "vehiclesPage", allEntries = true),
                    @CacheEvict(value = "deletedVehiclesPage", allEntries = true)
            }
    )
    @Override
    public VehicleDTO restoreVehicle(UUID id) {
        Vehicle vehicle = vehicleRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.VEHICLE_NOT_FOUND));

        if (!vehicle.isDeleted()) {
            throw new BusinessException(FleetErrorCode.VEHICLE_NOT_DELETED);
        }

        vehicle.restore();
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // Get depot data
        DepotDTO depotDTO = depotService.getDepot(savedVehicle.getDepotId());
        if (depotDTO == null) {
            throw new BusinessException(FleetErrorCode.VEHICLE_DEPOT_NOT_FOUND);
        }

        return new VehicleDTO(
                savedVehicle.getId(),
                savedVehicle.getName(),
                savedVehicle.getLicensePlate(),
                savedVehicle.getFuelType(),
                savedVehicle.getCapacity(),
                savedVehicle.getFuelConsumptionRate(),
                depotDTO
        );
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "vehiclesById", key = "#id"),
                    @CacheEvict(value = "vehiclesPage", allEntries = true),
                    @CacheEvict(value = "deletedVehiclesPage", allEntries = true)
            }
    )
    @Override
    public void hardDeleteVehicle(UUID id) {
        Vehicle vehicle = vehicleRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.VEHICLE_NOT_FOUND));

        vehicleRepository.delete(vehicle);
    }

    @Cacheable(value = "vehiclesById", key = "#id")
    @Override
    public VehicleDTO getVehicle(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.VEHICLE_NOT_FOUND));

        // Get depot data
        DepotDTO depotDTO = depotService.getDepot(vehicle.getDepotId());
        if (depotDTO == null) {
            throw new BusinessException(FleetErrorCode.VEHICLE_DEPOT_NOT_FOUND);
        }

        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getLicensePlate(),
                vehicle.getFuelType(),
                vehicle.getCapacity(),
                vehicle.getFuelConsumptionRate(),
                depotDTO
        );
    }

    @Cacheable(value = "vehiclesPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<VehicleDTO> getVehicles(int page, int size) {
        List<Vehicle> vehicles = vehicleRepository.findAll(PageRequest.of(page, size)).getContent();

        return vehicles.stream()
                .map(vehicle -> {
                    DepotDTO depotDTO = depotService.getDepot(vehicle.getDepotId());
                    return new VehicleDTO(
                            vehicle.getId(),
                            vehicle.getName(),
                            vehicle.getLicensePlate(),
                            vehicle.getFuelType(),
                            vehicle.getCapacity(),
                            vehicle.getFuelConsumptionRate(),
                            depotDTO
                    );
                }).toList();
    }

    @Override
    public long countVehicles() {
        return vehicleRepository.count();
    }

    @Cacheable(value = "deletedVehiclesPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<VehicleDTO> getDeletedVehicles(int page, int size) {
        List<Vehicle> deletedVehicles = vehicleRepository.findAllDeleted(PageRequest.of(page, size)).getContent();

        return deletedVehicles.stream()
                .map(vehicle -> {
                    DepotDTO depotDTO = depotService.getDepot(vehicle.getDepotId());
                    return new VehicleDTO(
                            vehicle.getId(),
                            vehicle.getName(),
                            vehicle.getLicensePlate(),
                            vehicle.getFuelType(),
                            vehicle.getCapacity(),
                            vehicle.getFuelConsumptionRate(),
                            depotDTO
                    );
                }).toList();
    }

    @Override
    public long countDeletedVehicles() {
        return vehicleRepository.countDeleted();
    }
}

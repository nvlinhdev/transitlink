package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.DriverDTO;
import vn.edu.fpt.transitlink.identity.dto.DriverInfo;
import vn.edu.fpt.transitlink.identity.request.CreateDriverRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateDriverRequest;

import java.util.List;
import java.util.UUID;

public interface DriverService {
    // Create and manage drivers
    DriverDTO createDriver(CreateDriverRequest request);
    DriverDTO getDriverById(UUID id);
    DriverDTO updateDriver(UUID id, UpdateDriverRequest request);
    DriverDTO deleteDriver(UUID deleteId, UUID deletedBy);
    DriverDTO restoreDriver(UUID id);
    UUID getAccountIdByDriverId(UUID driverId);

    DriverInfo getDriverInfoById(UUID driverId);
    List<DriverInfo> getDriverInfosByIds(List<UUID> driverIds);

    // List and paginate drivers
    List<DriverDTO> getDrivers(int page, int size);
    long countDrivers();
    List<DriverDTO> getDeletedDrivers(int page, int size);
    long countDeletedDrivers();

    // Current driver operations
    DriverDTO getCurrentDriverByAccountId(UUID accountId);
    DriverDTO updateCurrentDriver(UUID accountId, UpdateDriverRequest request);
}

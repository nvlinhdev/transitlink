package vn.edu.fpt.transitlink.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
import vn.edu.fpt.transitlink.fleet.service.DepotService;
import vn.edu.fpt.transitlink.identity.dto.AccountDTO;
import vn.edu.fpt.transitlink.identity.dto.DriverDTO;
import vn.edu.fpt.transitlink.identity.dto.DriverInfo;
import vn.edu.fpt.transitlink.identity.entity.Driver;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.identity.repository.DriverRepository;
import vn.edu.fpt.transitlink.identity.request.CreateDriverRequest;
import vn.edu.fpt.transitlink.identity.request.UpdateDriverRequest;
import vn.edu.fpt.transitlink.identity.service.AccountService;
import vn.edu.fpt.transitlink.identity.service.DriverService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final AccountService accountService;
    private final DepotService depotService;

    @Caching(
            put = {@CachePut(value = "driversById", key = "#result.id")},
            evict = {@CacheEvict(value = "driversPage", allEntries = true)}
    )
    @Override
    @Transactional
    public DriverDTO createDriver(CreateDriverRequest request) {
        // Validate account exists
        AccountDTO account = accountService.getAccountById(request.accountId());

        // Check if account is already associated with a driver
        if (driverRepository.existsByAccountId(request.accountId())) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_ALREADY_HAS_DRIVER,
                    "Account is already associated with a driver");
        }

        // Validate depot exists if provided
        DepotDTO depot = null;
        if (request.depotId() != null) {
            depot = depotService.getDepot(request.depotId());
        }

        // Create new driver
        Driver driver = new Driver();
        driver.setAccountId(request.accountId());
        driver.setLicenseNumber(request.licenseNumber());
        driver.setLicenseClass(request.licenseClass());
        driver.setDepotId(request.depotId());

        Driver savedDriver = driverRepository.save(driver);

        return mapToDriverDTO(savedDriver, account, depot);
    }

    @Cacheable(value = "driversById", key = "#id")
    @Override
    public DriverDTO getDriverById(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND));

        return mapToDriverDTO(driver);
    }

    @Caching(
            put = {@CachePut(value = "driversById", key = "#id")},
            evict = {@CacheEvict(value = "driversPage", allEntries = true)}
    )
    @Override
    @Transactional
    public DriverDTO updateDriver(UUID id, UpdateDriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND));

        // Validate depot if provided
        if (request.depotId() != null) {
            depotService.getDepot(request.depotId()); // This will throw exception if depot not found
        }

        // Update driver fields
        driver.setLicenseNumber(request.licenseNumber());
        driver.setLicenseClass(request.licenseClass());
        driver.setDepotId(request.depotId());

        Driver savedDriver = driverRepository.save(driver);

        return mapToDriverDTO(savedDriver);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "driversById", key = "#deleteId"),
                    @CacheEvict(value = "driversPage", allEntries = true),
                    @CacheEvict(value = "deletedDriversPage", allEntries = true)
            }
    )
    @Override
    @Transactional
    public DriverDTO deleteDriver(UUID deleteId, UUID deletedBy) {
        Driver driver = driverRepository.findById(deleteId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND));

        // Prevent self-deletion
        if (driver.getAccountId() != null && driver.getAccountId().equals(deletedBy)) {
            throw new BusinessException(AuthErrorCode.CANNOT_DELETE_OWN_ACCOUNT,
                    "Cannot delete your own driver profile");
        }
        driver.softDelete(deletedBy);
        Driver savedDriver = driverRepository.save(driver);
        accountService.deleteAccount(driver.getAccountId(), deletedBy);
        return mapToDriverDTO(savedDriver);
    }

    @Caching(
            put = {@CachePut(value = "driversById", key = "#restoreId")},
            evict = {
                    @CacheEvict(value = "driversPage", allEntries = true),
                    @CacheEvict(value = "deletedDriversPage", allEntries = true)
            }
    )
    @Override
    @Transactional
    public DriverDTO restoreDriver(UUID restoreId) {
        Driver driver = driverRepository.findByIdIncludingDeleted(restoreId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND));

        driver.restore();
        Driver savedDriver = driverRepository.save(driver);
        accountService.restoreAccount(driver.getAccountId());

        return mapToDriverDTO(savedDriver);
    }

    @Override
    public DriverInfo getDriverInfoById(UUID driverId) {
        return driverRepository.findById(driverId)
                .map(driver -> {
                    AccountDTO account = null;
                    if (driver.getAccountId() != null) {
                        try {
                            account = accountService.getAccountById(driver.getAccountId());
                        } catch (Exception e) {
                            // Account might be deleted or not found, handle gracefully
                            account = null;
                        }
                    }
                    return new DriverInfo(
                            driver.getId(),
                            account != null ? account.firstName() : null,
                            account != null ? account.lastName() : null,
                            account != null ? account.phoneNumber() : null,
                            account != null ? account.email() : null,
                            driver.getLicenseNumber(),
                            driver.getLicenseClass(),
                            account != null ? account.avatarUrl() : null
                    );
                })
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND));
    }

    @Override
    public List<DriverInfo> getDriverInfosByIds(List<UUID> driverIds) {
        if (driverIds == null || driverIds.isEmpty()) {
            return List.of();
        }

        return driverRepository.findAllByIdIn(driverIds)
                .stream()
                .map(driver -> {
                    AccountDTO account = null;
                    if (driver.getAccountId() != null) {
                        try {
                            account = accountService.getAccountById(driver.getAccountId());
                        } catch (Exception e) {
                            // Account might be deleted or not found, handle gracefully
                            account = null;
                        }
                    }
                    return new DriverInfo(
                            driver.getId(),
                            account != null ? account.firstName() : null,
                            account != null ? account.lastName() : null,
                            account != null ? account.phoneNumber() : null,
                            account != null ? account.email() : null,
                            driver.getLicenseNumber(),
                            driver.getLicenseClass(),
                            account != null ? account.avatarUrl() : null
                    );
                })
                .toList();
    }

    @Cacheable(value = "driversPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<DriverDTO> getDrivers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return driverRepository.findAll(pageable)
                .stream()
                .map(this::mapToDriverDTO)
                .toList();
    }

    @Override
    public long countDrivers() {
        return driverRepository.count();
    }

    @Cacheable(value = "deletedDriversPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<DriverDTO> getDeletedDrivers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return driverRepository.findAllDeleted(pageable)
                .stream()
                .map(this::mapToDriverDTO)
                .toList();
    }

    @Override
    public long countDeletedDrivers() {
        return driverRepository.countDeleted();
    }

    @Override
    public DriverDTO getCurrentDriverByAccountId(UUID accountId) {
        // Validate account exists
        accountService.getAccountById(accountId);

        // Find driver by account ID using the repository method
        Driver driver = driverRepository.findAll()
                .stream()
                .filter(d -> accountId.equals(d.getAccountId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND,
                        "No driver profile found for this account"));

        return mapToDriverDTO(driver);
    }

    @Override
    @Transactional
    public DriverDTO updateCurrentDriver(UUID accountId, UpdateDriverRequest request) {
        // Validate account exists
        accountService.getAccountById(accountId);

        // Find driver by account ID
        Driver driver = driverRepository.findAll()
                .stream()
                .filter(d -> accountId.equals(d.getAccountId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(AuthErrorCode.DRIVER_NOT_FOUND,
                        "No driver profile found for this account"));

        // Validate depot if provided
        if (request.depotId() != null) {
            depotService.getDepot(request.depotId());
        }

        // Update only allowed fields for current user (no depot change for security)
        driver.setLicenseNumber(request.licenseNumber());
        driver.setLicenseClass(request.licenseClass());
        // Note: Depot assignment is typically managed by managers, not drivers themselves

        Driver savedDriver = driverRepository.save(driver);

        return mapToDriverDTO(savedDriver);
    }

    /**
     * Maps Driver entity to DriverDTO with all related data
     */
    private DriverDTO mapToDriverDTO(Driver driver) {
        AccountDTO accountDTO = null;
        if (driver.getAccountId() != null) {
            try {
                accountDTO = accountService.getAccountById(driver.getAccountId());
            } catch (Exception e) {
                // Account might be deleted or not found, handle gracefully
                accountDTO = null;
            }
        }

        DepotDTO depotDTO = null;
        if (driver.getDepotId() != null) {
            try {
                depotDTO = depotService.getDepot(driver.getDepotId());
            } catch (Exception e) {
                // Depot might be deleted or not found, handle gracefully
                depotDTO = null;
            }
        }

        return new DriverDTO(
                driver.getId(),
                driver.getLicenseNumber(),
                driver.getLicenseClass(),
                accountDTO,
                depotDTO
        );
    }

    /**
     * Maps Driver entity to DriverDTO with provided account and depot data
     */
    private DriverDTO mapToDriverDTO(Driver driver, AccountDTO account, DepotDTO depot) {
        return new DriverDTO(
                driver.getId(),
                driver.getLicenseNumber(),
                driver.getLicenseClass(),
                account,
                depot
        );
    }
}

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
import vn.edu.fpt.transitlink.identity.dto.AccountDTO;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.entity.Passenger;
import vn.edu.fpt.transitlink.identity.enumeration.AuthErrorCode;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.repository.PassengerRepository;
import vn.edu.fpt.transitlink.identity.request.CreateAccountRequest;
import vn.edu.fpt.transitlink.identity.request.CreatePassengerRequest;
import vn.edu.fpt.transitlink.identity.request.ImportPassengerRequest;
import vn.edu.fpt.transitlink.identity.request.UpdatePassengerRequest;
import vn.edu.fpt.transitlink.identity.service.AccountService;
import vn.edu.fpt.transitlink.identity.service.PassengerService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    private final AccountService accountService;

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#result.id")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO createPassenger(CreatePassengerRequest request) {
        // Validate account exists
        AccountDTO account = accountService.getAccountById(request.accountId());

        // Check if account is already associated with a passenger
        if (passengerRepository.existsByAccountId(request.accountId())) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_ALREADY_HAS_PASSENGER,
                    "Account is already associated with a passenger");
        }

        // Validate trip counts if provided
        if (request.totalCompletedTrips() != null && request.totalCompletedTrips() < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total completed trips must be non-negative");
        }
        if (request.totalCancelledTrips() != null && request.totalCancelledTrips() < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total cancelled trips must be non-negative");
        }

        // Create new passenger
        Passenger passenger = new Passenger();
        passenger.setAccountId(request.accountId());
        passenger.setTotalCompletedTrips(request.totalCompletedTrips() != null ? request.totalCompletedTrips() : 0);
        passenger.setTotalCancelledTrips(request.totalCancelledTrips() != null ? request.totalCancelledTrips() : 0);

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger, account);
    }

    @Cacheable(value = "passengersById", key = "#id")
    @Override
    public PassengerDTO getPassengerById(UUID id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        return mapToPassengerDTO(passenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#id")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO updatePassenger(UUID id, UpdatePassengerRequest request) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        // Validate trip counts if provided
        if (request.totalCompletedTrips() != null && request.totalCompletedTrips() < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total completed trips must be non-negative");
        }
        if (request.totalCancelledTrips() != null && request.totalCancelledTrips() < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total cancelled trips must be non-negative");
        }

        // Update passenger fields
        if (request.totalCompletedTrips() != null) {
            passenger.setTotalCompletedTrips(request.totalCompletedTrips());
        }
        if (request.totalCancelledTrips() != null) {
            passenger.setTotalCancelledTrips(request.totalCancelledTrips());
        }

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "passengersById", key = "#deleteId"),
                    @CacheEvict(value = "passengersPage", allEntries = true),
                    @CacheEvict(value = "deletedPassengersPage", allEntries = true)
            }
    )
    @Override
    @Transactional
    public PassengerDTO deletePassenger(UUID deleteId, UUID deletedBy) {
        Passenger passenger = passengerRepository.findById(deleteId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        // Prevent self-deletion
        if (passenger.getAccountId() != null && passenger.getAccountId().equals(deletedBy)) {
            throw new BusinessException(AuthErrorCode.CANNOT_DELETE_OWN_ACCOUNT,
                    "Cannot delete your own passenger profile");
        }

        passenger.softDelete(deletedBy);
        Passenger savedPassenger = passengerRepository.save(passenger);

        // Also delete the associated account
        accountService.deleteAccount(passenger.getAccountId(), deletedBy);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#restoreId")},
            evict = {
                    @CacheEvict(value = "passengersPage", allEntries = true),
                    @CacheEvict(value = "deletedPassengersPage", allEntries = true)
            }
    )
    @Override
    @Transactional
    public PassengerDTO restorePassenger(UUID restoreId) {
        Passenger passenger = passengerRepository.findByIdIncludingDeleted(restoreId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        passenger.restore();
        Passenger savedPassenger = passengerRepository.save(passenger);

        // Also restore the associated account
        accountService.restoreAccount(passenger.getAccountId());

        return mapToPassengerDTO(savedPassenger);
    }

    @Cacheable(value = "passengersPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<PassengerDTO> getPassengers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return passengerRepository.findAll(pageable)
                .stream()
                .map(this::mapToPassengerDTO)
                .toList();
    }

    @Override
    public long countPassengers() {
        return passengerRepository.count();
    }

    @Cacheable(value = "deletedPassengersPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<PassengerDTO> getDeletedPassengers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return passengerRepository.findAllDeleted(pageable)
                .stream()
                .map(passenger -> mapToPassengerDTO(passenger))
                .toList();
    }

    @Override
    public long countDeletedPassengers() {
        return passengerRepository.countDeleted();
    }

    @Override
    public PassengerDTO getCurrentPassengerByAccountId(UUID accountId) {
        // Validate account exists
        accountService.getAccountById(accountId);

        // Find passenger by account ID
        Passenger passenger = passengerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND,
                        "No passenger profile found for this account"));

        return mapToPassengerDTO(passenger);
    }

    @Override
    @Transactional
    public PassengerDTO updateCurrentPassenger(UUID accountId, UpdatePassengerRequest request) {
        // Validate account exists
        accountService.getAccountById(accountId);

        // Find passenger by account ID
        Passenger passenger = passengerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND,
                        "No passenger profile found for this account"));

        // Validate trip counts if provided
        if (request.totalCompletedTrips() != null && request.totalCompletedTrips() < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total completed trips must be non-negative");
        }
        if (request.totalCancelledTrips() != null && request.totalCancelledTrips() < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total cancelled trips must be non-negative");
        }

        // Update passenger fields
        if (request.totalCompletedTrips() != null) {
            passenger.setTotalCompletedTrips(request.totalCompletedTrips());
        }
        if (request.totalCancelledTrips() != null) {
            passenger.setTotalCancelledTrips(request.totalCancelledTrips());
        }

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#passengerId")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO incrementCompletedTrips(UUID passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        int currentTrips = passenger.getTotalCompletedTrips() != null ? passenger.getTotalCompletedTrips() : 0;
        passenger.setTotalCompletedTrips(currentTrips + 1);

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#passengerId")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO incrementCancelledTrips(UUID passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        int currentTrips = passenger.getTotalCancelledTrips() != null ? passenger.getTotalCancelledTrips() : 0;
        passenger.setTotalCancelledTrips(currentTrips + 1);

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#passengerId")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO setTotalCompletedTrips(UUID passengerId, Integer totalTrips) {
        if (totalTrips < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total completed trips must be non-negative");
        }

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        passenger.setTotalCompletedTrips(totalTrips);

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#passengerId")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO setTotalCancelledTrips(UUID passengerId, Integer totalTrips) {
        if (totalTrips < 0) {
            throw new BusinessException(AuthErrorCode.INVALID_TRIP_COUNT,
                    "Total cancelled trips must be non-negative");
        }

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        passenger.setTotalCancelledTrips(totalTrips);

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            put = {@CachePut(value = "passengersById", key = "#passengerId")},
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public PassengerDTO resetTripStatistics(UUID passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.PASSENGER_NOT_FOUND));

        passenger.setTotalCompletedTrips(0);
        passenger.setTotalCancelledTrips(0);

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger);
    }

    @Caching(
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public List<PassengerDTO> importPassengers(List<CreatePassengerRequest> requests) {
        List<PassengerDTO> createdPassengers = new ArrayList<>();

        for (CreatePassengerRequest request : requests) {
            try {
                PassengerDTO passenger = createPassenger(request);
                createdPassengers.add(passenger);
            } catch (BusinessException e) {
                // Log the error and continue with next passenger
                // You might want to return a result object with success/failure details
                // For now, we skip failed imports
            }
        }

        return createdPassengers;
    }

    @Caching(
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public List<PassengerDTO> importPassengersWithAccountData(List<ImportPassengerRequest> requests) {
        List<PassengerDTO> createdPassengers = new ArrayList<>();

        for (ImportPassengerRequest request : requests) {
            try {
                // Create account first
                CreateAccountRequest accountRequest = new CreateAccountRequest(
                        request.email(),
                        request.password() != null ? request.password() : "TempPassword123!", // Default password if not provided
                        request.firstName(),
                        request.lastName(),
                        request.gender(),
                        request.birthDate(),
                        request.phoneNumber(),
                        request.zaloPhoneNumber(),
                        request.avatarUrl(),
                        Set.of(RoleName.PASSENGER) // Default role for passengers
                );

                AccountDTO createdAccount = accountService.createAccount(accountRequest);

                // Create passenger with the new account ID
                CreatePassengerRequest passengerRequest = new CreatePassengerRequest(
                        createdAccount.id(),
                        request.totalCompletedTrips() != null ? request.totalCompletedTrips() : 0,
                        request.totalCancelledTrips() != null ? request.totalCancelledTrips() : 0
                );

                PassengerDTO passenger = createPassenger(passengerRequest);
                createdPassengers.add(passenger);

                // Send verification email if requested
                if (request.sendVerificationEmail() != null && request.sendVerificationEmail()) {
                    // This would typically be handled by the auth service or email service
                    // For now, we'll assume the account creation process handles this
                }

            } catch (BusinessException e) {
                // Log the error and continue with next passenger
                // You might want to return a result object with success/failure details
                // For now, we skip failed imports
            }
        }

        return createdPassengers;
    }

    @Override
    @Transactional
    public PassengerDTO getOrCreatePassengerByAccountId(UUID accountId) {
        // Try to find existing passenger first
        try {
            return getCurrentPassengerByAccountId(accountId);
        } catch (BusinessException e) {
            if (e.getErrorCode() == AuthErrorCode.PASSENGER_NOT_FOUND) {
                // Create new passenger if not found
                CreatePassengerRequest request = new CreatePassengerRequest(accountId, 0, 0);
                return createPassenger(request);
            }
            throw e; // Re-throw other exceptions
        }
    }

    /**
     * Maps Passenger entity to PassengerDTO with all related data
     */
    private PassengerDTO mapToPassengerDTO(Passenger passenger) {
        AccountDTO accountDTO = null;
        if (passenger.getAccountId() != null) {
            try {
                accountDTO = accountService.getAccountById(passenger.getAccountId());
            } catch (Exception e) {
                // Account might be deleted or not found, handle gracefully
                accountDTO = null;
            }
        }

        return new PassengerDTO(
                passenger.getId(),
                passenger.getTotalCompletedTrips(),
                passenger.getTotalCancelledTrips(),
                accountDTO
        );
    }

    /**
     * Maps Passenger entity to PassengerDTO with provided account data
     */
    private PassengerDTO mapToPassengerDTO(Passenger passenger, AccountDTO account) {
        return new PassengerDTO(
                passenger.getId(),
                passenger.getTotalCompletedTrips(),
                passenger.getTotalCancelledTrips(),
                account
        );
    }
}

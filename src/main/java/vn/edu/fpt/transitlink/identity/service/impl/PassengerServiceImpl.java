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
import vn.edu.fpt.transitlink.identity.repository.PassengerRepository;
import vn.edu.fpt.transitlink.identity.request.CreatePassengerRequest;
import vn.edu.fpt.transitlink.identity.request.ImportAccountRequest;
import vn.edu.fpt.transitlink.identity.request.ImportPassengerRequest;
import vn.edu.fpt.transitlink.identity.request.UpdatePassengerRequest;
import vn.edu.fpt.transitlink.identity.service.AccountService;
import vn.edu.fpt.transitlink.identity.service.PassengerService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        AccountDTO account = accountService.getAccountByEmail(request.accountInfo().email());

        if (account == null) {
            account = accountService.createAccount(request.accountInfo());
        }

        if (passengerRepository.findByAccountId(account.id()).isPresent()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_ALREADY_HAS_PASSENGER,
                    "Passenger profile already exists for this account");
        }

        // Create new passenger
        Passenger passenger = new Passenger();
        passenger.setAccountId(account.id());
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

        // Update account info if provided - delegate to AccountService
        if (request.accountInfo() != null) {
            accountService.updateAccount(passenger.getAccountId(), request.accountInfo());
        }

        passenger.setTotalCompletedTrips(request.totalCompletedTrips());
        passenger.setTotalCancelledTrips(request.totalCancelledTrips());
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
        AccountDTO deletedAccount = accountService.deleteAccount(passenger.getAccountId(), deletedBy);

        return mapToPassengerDTO(savedPassenger, deletedAccount);
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
        AccountDTO restoredAccount = accountService.restoreAccount(passenger.getAccountId());

        return mapToPassengerDTO(savedPassenger, restoredAccount);
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
                .map(this::mapToPassengerDTO)
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

        AccountDTO updatedAccount = accountService.updateAccount(passenger.getAccountId(), request.accountInfo());

        passenger.setTotalCompletedTrips(request.totalCompletedTrips());
        passenger.setTotalCancelledTrips(request.totalCancelledTrips());

        Passenger savedPassenger = passengerRepository.save(passenger);

        return mapToPassengerDTO(savedPassenger, updatedAccount);
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
            evict = {@CacheEvict(value = "passengersPage", allEntries = true)}
    )
    @Override
    @Transactional
    public List<PassengerDTO> importPassengers(List<ImportPassengerRequest> requests) {
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        // Step 1: Extract account info and import accounts in bulk
        List<ImportAccountRequest> accountRequests = requests.stream()
                .map(ImportPassengerRequest::accountInfo)
                .toList();

        // Bulk import accounts (returns existing + newly created accounts)
        List<AccountDTO> accountResults = accountService.importAccounts(accountRequests);

        // Step 2: Extract account IDs from the imported accounts
        List<UUID> accountIds = accountResults.stream()
                .map(AccountDTO::id)
                .toList();

        // Step 3: Bulk check which accounts already have passengers
        List<Passenger> existingPassengers = passengerRepository.findByAccountIdsIn(accountIds);

        // Create lookup map for existing passengers by accountId
        Map<UUID, Passenger> existingPassengerMap = existingPassengers.stream()
                .collect(Collectors.toMap(
                        Passenger::getAccountId,
                        passenger -> passenger,
                        (existing, replacement) -> existing // Keep existing if duplicate
                ));

        List<PassengerDTO> results = new ArrayList<>();
        List<Passenger> newPassengersToSave = new ArrayList<>();

        // Step 4: Process each import request
        for (int i = 0; i < requests.size(); i++) {
            ImportPassengerRequest request = requests.get(i);
            AccountDTO account = accountResults.get(i);
            UUID accountId = account.id();

            Passenger existingPassenger = existingPassengerMap.get(accountId);

            if (existingPassenger != null) {
                // Passenger already exists, return existing one
                results.add(mapToPassengerDTO(existingPassenger, account));
            } else {
                // Create new passenger
                Passenger newPassenger = createNewPassengerFromImport(request, accountId);
                newPassengersToSave.add(newPassenger);
                results.add(mapToPassengerDTO(newPassenger, account));
            }
        }

        // Step 5: Bulk insert new passengers
        if (!newPassengersToSave.isEmpty()) {
            passengerRepository.saveAll(newPassengersToSave);
        }

        return results;
    }

    private Passenger createNewPassengerFromImport(ImportPassengerRequest request, UUID accountId) {
        Passenger passenger = new Passenger();
        passenger.setAccountId(accountId);

        // Set trip statistics with defaults
        passenger.setTotalCompletedTrips(
                request.totalCompletedTrips() != null ? request.totalCompletedTrips() : 0
        );
        passenger.setTotalCancelledTrips(
                request.totalCancelledTrips() != null ? request.totalCancelledTrips() : 0
        );

        return passenger;
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

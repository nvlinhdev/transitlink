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
import vn.edu.fpt.transitlink.identity.dto.ImportAccountResultDTO;
import vn.edu.fpt.transitlink.identity.dto.ImportPassengerResultDTO;
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
import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.*;
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
        passenger.setId(UUID.randomUUID());
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

    @Override
    public List<PassengerDTO> getPassengersByIds(List<UUID> passengerIds) {
        List<Passenger> passengers = passengerRepository.findAllById(passengerIds);
        return mapToPassengerDTOList(passengers);
    }

    private List<PassengerDTO> mapToPassengerDTOList(List<Passenger> passengers) {
        List<UUID> accountIds = passengers.stream()
                .map(Passenger::getAccountId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<AccountDTO> accounts = accountService.getAccountsByIds(accountIds);

        return passengers.stream()
                .map(passenger -> {
                    AccountDTO account = accounts.stream()
                            .filter(acc -> acc.id().equals(passenger.getAccountId()))
                            .findFirst()
                            .orElse(null);
                    return mapToPassengerDTO(passenger, account);
                })
                .toList();
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
    public ImportPassengerResultDTO importPassengers(List<ImportPassengerRequest> requests) {
        if (requests.isEmpty()) {
            return new ImportPassengerResultDTO(0, 0, List.of(), List.of());
        }

        List<PassengerDTO> results = new ArrayList<>();
        List<ImportErrorDTO> errors = new ArrayList<>();

        // Step 1: Import accounts
        List<ImportAccountRequest> accountRequests = requests.stream()
                .map(ImportPassengerRequest::accountInfo)
                .toList();

        ImportAccountResultDTO accountImportResult = accountService.importAccounts(accountRequests);
        List<AccountDTO> successfulAccounts = accountImportResult.successfulList();

        // Create mapping: request index → account (nếu account import thành công)
        Map<Integer, AccountDTO> requestIndexToAccountMap = new HashMap<>();

        // Map successful accounts back to their original request index
        for (int i = 0; i < requests.size(); i++) {
            ImportAccountRequest originalAccountRequest = requests.get(i).accountInfo();

            // Tìm account tương ứng trong successful list
            AccountDTO matchedAccount = successfulAccounts.stream()
                    .filter(acc -> acc.email().equals(originalAccountRequest.email()))
                    .findFirst()
                    .orElse(null);

            if (matchedAccount != null) {
                requestIndexToAccountMap.put(i, matchedAccount);
            } else {
                // Account import failed cho request này
                errors.add(new ImportErrorDTO(i, "Account import failed for email: " + originalAccountRequest.email()));
            }
        }

        // Step 2: Bulk check existing passengers (chỉ với successful accounts)
        List<UUID> successfulAccountIds = requestIndexToAccountMap.values().stream()
                .map(AccountDTO::id)
                .toList();

        Map<UUID, Passenger> existingPassengerMap = Collections.emptyMap();
        if (!successfulAccountIds.isEmpty()) {
            List<Passenger> existingPassengers = passengerRepository.findByAccountIdsIn(successfulAccountIds);
            existingPassengerMap = existingPassengers.stream()
                    .collect(Collectors.toMap(
                            Passenger::getAccountId,
                            passenger -> passenger,
                            (existing, replacement) -> existing
                    ));
        }

        // Step 3: Process passengers và prepare data cho bulk save
        Map<Passenger, Integer> passengerToRequestIndexMap = new HashMap<>();
        Map<Passenger, AccountDTO> passengerToAccountMap = new HashMap<>();
        List<Passenger> newPassengersToSave = new ArrayList<>();

        for (Map.Entry<Integer, AccountDTO> entry : requestIndexToAccountMap.entrySet()) {
            int requestIndex = entry.getKey();
            AccountDTO account = entry.getValue();
            ImportPassengerRequest request = requests.get(requestIndex);

            try {
                Passenger existingPassenger = existingPassengerMap.get(account.id());

                if (existingPassenger != null) {
                    // Existing passenger
                    results.add(mapToPassengerDTO(existingPassenger, account));
                } else {
                    // New passenger
                    Passenger newPassenger = createNewPassengerFromImport(request, account.id());
                    newPassengersToSave.add(newPassenger);

                    // Track mappings cho bulk save
                    passengerToRequestIndexMap.put(newPassenger, requestIndex);
                    passengerToAccountMap.put(newPassenger, account);
                }
            } catch (Exception ex) {
                errors.add(new ImportErrorDTO(requestIndex, "Failed to process passenger: " + ex.getMessage()));
            }
        }

        // Step 4: Bulk save new passengers
        if (!newPassengersToSave.isEmpty()) {
            try {
                List<Passenger> savedPassengers = passengerRepository.saveAll(newPassengersToSave);

                // Create mapping: accountId → saved passenger để match với original passengers
                Map<UUID, Passenger> accountIdToSavedPassengerMap = savedPassengers.stream()
                        .collect(Collectors.toMap(Passenger::getAccountId, passenger -> passenger));

                // Add saved passengers to results theo đúng order
                for (Passenger originalPassenger : newPassengersToSave) {
                    Passenger savedPassenger = accountIdToSavedPassengerMap.get(originalPassenger.getAccountId());
                    AccountDTO account = passengerToAccountMap.get(originalPassenger);

                    if (savedPassenger != null && account != null) {
                        results.add(mapToPassengerDTO(savedPassenger, account));
                    }
                }

            } catch (Exception ex) {
                // Fallback: save individually
                handleIndividualPassengerSave(newPassengersToSave, passengerToRequestIndexMap,
                        passengerToAccountMap, results, errors);
            }
        }

        // Add account import errors to final result
        errors.addAll(accountImportResult.failedList());

        int successful = results.size();
        int failed = requests.size() - successful;

        return new ImportPassengerResultDTO(successful, failed, errors, results);
    }

    private void handleIndividualPassengerSave(
            List<Passenger> newPassengersToSave,
            Map<Passenger, Integer> passengerToRequestIndexMap,
            Map<Passenger, AccountDTO> passengerToAccountMap,
            List<PassengerDTO> results,
            List<ImportErrorDTO> errors) {

        for (Passenger newPassenger : newPassengersToSave) {
            Integer requestIndex = passengerToRequestIndexMap.get(newPassenger);
            AccountDTO account = passengerToAccountMap.get(newPassenger);

            try {
                Passenger saved = passengerRepository.save(newPassenger);
                results.add(mapToPassengerDTO(saved, account));

            } catch (Exception innerEx) {
                errors.add(new ImportErrorDTO(requestIndex != null ? requestIndex : -1,
                        "Failed to save passenger for accountId " + newPassenger.getAccountId() +
                                ": " + innerEx.getMessage()));
            }
        }
    }

    private Passenger createNewPassengerFromImport(ImportPassengerRequest request, UUID accountId) {
        Passenger passenger = new Passenger();
        passenger.setId(UUID.randomUUID());
        passenger.setAccountId(accountId);
        passenger.setTotalCompletedTrips(request.totalCompletedTrips() != null ? request.totalCompletedTrips() : 0);
        passenger.setTotalCancelledTrips(request.totalCancelledTrips() != null ? request.totalCancelledTrips() : 0);
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

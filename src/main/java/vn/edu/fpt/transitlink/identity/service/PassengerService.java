package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.request.CreatePassengerRequest;
import vn.edu.fpt.transitlink.identity.request.UpdatePassengerRequest;
import vn.edu.fpt.transitlink.identity.request.ImportPassengerRequest;

import java.util.List;
import java.util.UUID;

public interface PassengerService {
    // Create and manage passengers
    PassengerDTO createPassenger(CreatePassengerRequest request);
    PassengerDTO getPassengerById(UUID id);
    PassengerDTO updatePassenger(UUID id, UpdatePassengerRequest request);
    PassengerDTO deletePassenger(UUID deleteId, UUID deletedBy);
    PassengerDTO restorePassenger(UUID id);

    // List and paginate passengers
    List<PassengerDTO> getPassengers(int page, int size);
    long countPassengers();
    List<PassengerDTO> getDeletedPassengers(int page, int size);
    long countDeletedPassengers();

    // Current passenger operations
    PassengerDTO getCurrentPassengerByAccountId(UUID accountId);
    PassengerDTO updateCurrentPassenger(UUID accountId, UpdatePassengerRequest request);

    // Trip statistics management
    PassengerDTO incrementCompletedTrips(UUID passengerId);
    PassengerDTO incrementCancelledTrips(UUID passengerId);
    PassengerDTO setTotalCompletedTrips(UUID passengerId, Integer totalTrips);
    PassengerDTO setTotalCancelledTrips(UUID passengerId, Integer totalTrips);
    PassengerDTO resetTripStatistics(UUID passengerId);

    // Bulk operations
    List<PassengerDTO> importPassengers(List<CreatePassengerRequest> requests);
    List<PassengerDTO> importPassengersWithAccountData(List<ImportPassengerRequest> requests);
    PassengerDTO getOrCreatePassengerByAccountId(UUID accountId);
}

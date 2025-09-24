package vn.edu.fpt.transitlink.trip.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.transitlink.trip.dto.ImportJourneyResultDTO;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDetailForPassengerDTO;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneySummaryDTO;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.request.CreatePassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.ImportPassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.SearchPassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.UpdatePassengerJourneyRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface PassengerJourneyService {

    // CRUD Operations
    PassengerJourneyDTO createPassengerJourney(CreatePassengerJourneyRequest request);
    PassengerJourneyDTO getPassengerJourneyById(UUID id);
    PassengerJourneyDTO updatePassengerJourney(UUID id, UpdatePassengerJourneyRequest request);
    PassengerJourneyDTO deletePassengerJourney(UUID id, UUID deletedBy);
    PassengerJourneyDTO restorePassengerJourney(UUID id);


    // List and paginate operations
    List<PassengerJourneyDTO> getPassengerJourneys(int page, int size);
    long countPassengerJourneys();
    List<PassengerJourneyDTO> getDeletedPassengerJourneys(int page, int size);
    long countDeletedPassengerJourneys();
    List<PassengerJourneyDTO> getAllPassengerJourneysByIds(List<UUID> ids);

    // Status-based filtering
    List<PassengerJourneyDTO> getPassengerJourneysByStatus(JourneyStatus status, int page, int size);
    List<PassengerJourneyDTO> getUnscheduledJourneys(int page, int size);

    // Date-based queries for dispatchers
    List<PassengerJourneyDTO> getJourneysByDateRange(OffsetDateTime startDate, OffsetDateTime endDate, int page, int size);
    List<PassengerJourneyDTO> getJourneysForToday(int page, int size);
    List<PassengerJourneyDTO> getJourneysForWeek(OffsetDateTime weekStart, int page, int size);
    List<PassengerJourneyDTO> getJourneysForMonth(int year, int month, int page, int size);

    // Passenger-specific operations
    List<PassengerJourneySummaryDTO> getCurrentPassengerJourneys(UUID accountId, int page, int size);
    PassengerJourneyDetailForPassengerDTO getPassengerJourneyDetail(UUID journeyId);
    List<PassengerJourneyDTO> getPassengerCompletedJourneys(UUID accountId, int page, int size);
    List<PassengerJourneyDTO> getPassengerCancelledJourneys(UUID accountId, int page, int size);
    List<PassengerJourneyDTO> getPassengerJourneyHistory(UUID accountId, int page, int size);

    // Search operations
    List<PassengerJourneyDTO> searchPassengerJourneys(SearchPassengerJourneyRequest request, int page, int size);
    List<PassengerJourneyDTO> searchByPassengerNameOrEmail(String query, int page, int size);

    // Bulk operations
    ImportJourneyResultDTO importPassengerJourneysFromExcel(MultipartFile file, ImportPassengerJourneyRequest request);

    // OptimizationRoute service integration methods
    PassengerJourneyDTO updateJourneyStatus(UUID journeyId, JourneyStatus status);
    PassengerJourneyDTO setActualPickupTime(UUID journeyId, OffsetDateTime pickupTime);
    PassengerJourneyDTO setActualDropoffTime(UUID journeyId, OffsetDateTime dropoffTime);
    // Statistics
    long countJourneysByStatus(JourneyStatus status);
    long countJourneysByPassenger(UUID passengerId);
}

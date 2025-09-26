package vn.edu.fpt.transitlink.trip.service.impl;

import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.transitlink.identity.dto.ImportPassengerResultDTO;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.request.ImportAccountRequest;
import vn.edu.fpt.transitlink.identity.request.ImportPassengerRequest;
import vn.edu.fpt.transitlink.identity.service.PassengerService;
import vn.edu.fpt.transitlink.location.dto.ImportPlaceResultDTO;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.request.ImportPlaceRequest;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.shared.dto.ImportErrorDTO;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.shared.util.ExcelFileUtils;
import vn.edu.fpt.transitlink.trip.dto.*;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourney;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourneyDocument;
import vn.edu.fpt.transitlink.trip.entity.Stop;
import vn.edu.fpt.transitlink.trip.entity.StopJourneyMapping;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyType;
import vn.edu.fpt.transitlink.trip.enumeration.StopAction;
import vn.edu.fpt.transitlink.trip.exception.TripErrorCode;
import vn.edu.fpt.transitlink.trip.repository.PassengerJourneyESRepository;
import vn.edu.fpt.transitlink.trip.repository.PassengerJourneyRepository;
import vn.edu.fpt.transitlink.trip.request.*;
import vn.edu.fpt.transitlink.trip.service.PassengerJourneyService;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PassengerJourneyServiceImpl implements PassengerJourneyService {
    private final PassengerJourneyRepository passengerJourneyRepository;
    private final PassengerJourneyESRepository passengerJourneyESRepository;
    private final PlaceService placeService;
    private final PassengerService passengerService;

    @Override
    @Transactional
    public PassengerJourneyDTO createPassengerJourney(CreatePassengerJourneyRequest request) {
        try {
            // Validate pickup and dropoff places exist
            PlaceDTO pickupPlace = placeService.getPlace(request.pickupPlaceId());
            PlaceDTO dropoffPlace = placeService.getPlace(request.dropoffPlaceId());

            // Create or get passenger
            PassengerDTO passenger = passengerService.createPassenger(request.passengerInfo());

            // Create journey entity
            PassengerJourney journey = new PassengerJourney();
            journey.setPassengerId(passenger.id());
            journey.setPickupPlaceId(request.pickupPlaceId());
            journey.setDropoffPlaceId(request.dropoffPlaceId());
            journey.setMainStopArrivalTime(request.lastestStopArrivalTime());
            journey.setSeatCount(request.seatCount());
            journey.setStatus(JourneyStatus.NOT_SCHEDULED);

            // Save to database
            PassengerJourney savedJourney = passengerJourneyRepository.save(journey);

            // Index to Elasticsearch
            indexToElasticsearch(savedJourney, passenger, pickupPlace, dropoffPlace);

            return mapToDTO(savedJourney, passenger, pickupPlace, dropoffPlace);

        } catch (Exception e) {
            log.error("Failed to create passenger journey", e);
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_CREATION_FAILED, e.getMessage());
        }
    }

    @Override
    public PassengerJourneyDTO getPassengerJourneyById(UUID id) {
        PassengerJourney journey = passengerJourneyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        return mapToDTO(journey);
    }

    @Override
    @Transactional
    public PassengerJourneyDTO updatePassengerJourney(UUID id, UpdatePassengerJourneyRequest request) {
        PassengerJourney journey = passengerJourneyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        // Validate journey can be modified
        if (journey.getStatus() == JourneyStatus.COMPLETED || journey.getStatus() == JourneyStatus.CANCELED) {
            throw new BusinessException(TripErrorCode.JOURNEY_CANNOT_BE_MODIFIED);
        }

        try {
            // Update fields if provided
            if (request.pickupPlaceId() != null) {
                placeService.getPlace(request.pickupPlaceId()); // Validate exists
                journey.setPickupPlaceId(request.pickupPlaceId());
            }

            if (request.dropoffPlaceId() != null) {
                placeService.getPlace(request.dropoffPlaceId()); // Validate exists
                journey.setDropoffPlaceId(request.dropoffPlaceId());
            }

            if (request.mainStopArrivalTime() != null) {
                journey.setMainStopArrivalTime(request.mainStopArrivalTime());
            }

            if (request.seatCount() != null) {
                journey.setSeatCount(request.seatCount());
            }

            if (request.journeyType() != null) {
                journey.setJourneyType(request.journeyType());
            }

            if (request.geometry() != null) {
                journey.setGeometry(request.geometry());
            }

            if (request.plannedPickupTime() != null) {
                journey.setPlannedPickupTime(request.plannedPickupTime());
            }

            if (request.actualPickupTime() != null) {
                journey.setActualPickupTime(request.actualPickupTime());
            }

            if (request.plannedDropoffTime() != null) {
                journey.setPlannedDropoffTime(request.plannedDropoffTime());
            }

            if (request.actualDropoffTime() != null) {
                journey.setActualDropoffTime(request.actualDropoffTime());
            }

            if (request.status() != null) {
                journey.setStatus(request.status());
            }

            // Update passenger info if provided
            if (request.passengerInfo() != null) {
                passengerService.updatePassenger(journey.getPassengerId(), request.passengerInfo());
            }

            PassengerJourney savedJourney = passengerJourneyRepository.save(journey);

            // Update Elasticsearch index
            PassengerDTO passenger = passengerService.getPassengerById(savedJourney.getPassengerId());
            PlaceDTO pickupPlace = placeService.getPlace(savedJourney.getPickupPlaceId());
            PlaceDTO dropoffPlace = placeService.getPlace(savedJourney.getDropoffPlaceId());
            indexToElasticsearch(savedJourney, passenger, pickupPlace, dropoffPlace);

            return mapToDTO(savedJourney, passenger, pickupPlace, dropoffPlace);

        } catch (Exception e) {
            log.error("Failed to update passenger journey with id: {}", id, e);
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public PassengerJourneyDTO deletePassengerJourney(UUID id, UUID deletedBy) {
        PassengerJourney journey = passengerJourneyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        try {
            journey.setDeletedBy(deletedBy);
            passengerJourneyRepository.save(journey);

            // Remove from Elasticsearch
            passengerJourneyESRepository.deleteById(id);

            return mapToDTO(journey);

        } catch (Exception e) {
            log.error("Failed to delete passenger journey with id: {}", id, e);
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_DELETION_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public PassengerJourneyDTO restorePassengerJourney(UUID id) {
        PassengerJourney journey = passengerJourneyRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));
        journey.restore();

        passengerJourneyRepository.save(journey);

        // Re-index to Elasticsearch
        PassengerDTO passenger = passengerService.getPassengerById(journey.getPassengerId());
        PlaceDTO pickupPlace = placeService.getPlace(journey.getPickupPlaceId());
        PlaceDTO dropoffPlace = placeService.getPlace(journey.getDropoffPlaceId());
        indexToElasticsearch(journey, passenger, pickupPlace, dropoffPlace);

        return mapToDTO(journey, passenger, pickupPlace, dropoffPlace);
    }

    @Override
    public List<PassengerJourneyDTO> getPassengerJourneys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findAll(pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO).toList();
    }

    @Override
    public long countPassengerJourneys() {
        return passengerJourneyRepository.count();
    }

    @Override
    public List<PassengerJourneyDTO> getDeletedPassengerJourneys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("deletedAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findAllDeleted(pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public long countDeletedPassengerJourneys() {
        return passengerJourneyRepository.countDeleted();
    }

    @Override
    public List<PassengerJourneyDTO> getAllPassengerJourneysByIds(List<UUID> ids) {
        return passengerJourneyRepository.findAllById(ids).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> getPassengerJourneysByStatus(JourneyStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByStatus(status, pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> getUnscheduledJourneys(int page, int size) {
        return getPassengerJourneysByStatus(JourneyStatus.NOT_SCHEDULED, page, size);
    }

    @Override
    public List<PassengerJourneyDTO> getJourneysByDateRange(OffsetDateTime startDate, OffsetDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("mainStopArrivalTime").ascending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByMainStopArrivalTimeBetween(startDate, endDate, pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> getJourneysForToday(int page, int size) {
        OffsetDateTime startOfDay = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return getJourneysByDateRange(startOfDay, endOfDay, page, size);
    }

    @Override
    public List<PassengerJourneyDTO> getJourneysForWeek(OffsetDateTime weekStart, int page, int size) {
        OffsetDateTime weekEnd = weekStart.plusWeeks(1).minusNanos(1);
        return getJourneysByDateRange(weekStart, weekEnd, page, size);
    }

    @Override
    public List<PassengerJourneyDTO> getJourneysForMonth(int year, int month, int page, int size) {
        OffsetDateTime startOfMonth = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return getJourneysByDateRange(startOfMonth, endOfMonth, page, size);
    }

    @Override
    public List<PassengerJourneySummaryDTO> getCurrentPassengerJourneys(UUID accountId, int page, int size) {
        List<JourneyStatus> currentStatuses = Arrays.asList(
                JourneyStatus.NOT_SCHEDULED,
                JourneyStatus.SCHEDULED,
                JourneyStatus.IN_PROGRESS
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByAccountIdAndStatuses(accountId, currentStatuses, pageable);

        return journeyPage.getContent().stream()
                .map(journey -> new PassengerJourneySummaryDTO(
                        journey.getId(),
                        placeService.getPlace(journey.getPickupPlaceId()).name(),
                        placeService.getPlace(journey.getDropoffPlaceId()).name(),
                        journey.getMainStopArrivalTime(),
                        journey.getSeatCount(),
                        journey.getJourneyType(),
                        journey.getPlannedPickupTime(),
                        journey.getPlannedDropoffTime(),
                        journey.getStatus()
                ))
                .toList();
    }

    @Override
    public PassengerJourneyDetailForPassengerDTO getPassengerJourneyDetail(UUID journeyId) {
        PassengerJourney journey = passengerJourneyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        PlaceDTO pickupPlace = placeService.getPlace(journey.getPickupPlaceId());
        PlaceDTO dropoffPlace = placeService.getPlace(journey.getDropoffPlaceId());

        return new PassengerJourneyDetailForPassengerDTO(
                journey.getId(),
                pickupPlace,
                dropoffPlace,
                journey.getMainStopArrivalTime(),
                journey.getSeatCount(),
                journey.getJourneyType(),
                journey.getGeometry(),
                journey.getPlannedPickupTime(),
                journey.getPlannedDropoffTime(),
                journey.getStatus()
        );
    }

    @Override
    public List<PassengerJourneyDTO> getPassengerCompletedJourneys(UUID passengerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("actualDropoffTime").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByPassengerIdAndStatus(passengerId, JourneyStatus.COMPLETED, pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> getPassengerCancelledJourneys(UUID passengerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByPassengerIdAndStatus(passengerId, JourneyStatus.CANCELED, pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> getPassengerJourneyHistory(UUID passengerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByPassengerId(passengerId, pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> searchPassengerJourneys(SearchPassengerJourneyRequest request, int page, int size) {
        List<PassengerJourneyDocument> documents = new ArrayList<>();

        // Use Elasticsearch for complex searches
        if (request.query() != null && !request.query().trim().isEmpty()) {
            if (request.status() != null) {
                documents = passengerJourneyESRepository.findByPassengerAndStatus(request.query(), request.status().name());
            } else {
                documents = passengerJourneyESRepository.findByGeneralQuery(request.query());
            }
        } else if (request.status() != null) {
            documents = passengerJourneyESRepository.findByStatus(request.status().name());
        } else if (request.startDate() != null && request.endDate() != null) {
            documents = passengerJourneyESRepository.findByDateRange(
                    request.startDate().toString(),
                    request.endDate().toString()
            );
        }

        // Apply pagination manually since ES results might not be paginated
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, documents.size());

        if (startIndex >= documents.size()) {
            return Collections.emptyList();
        }

        return documents.subList(startIndex, endIndex).stream()
                .map(this::convertDocumentToDTO)
                .toList();
    }

    @Override
    public List<PassengerJourneyDTO> searchByPassengerNameOrEmail(String query, int page, int size) {
        List<PassengerJourneyDocument> documents = passengerJourneyESRepository.findByPassengerNameOrEmail(query);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, documents.size());

        if (startIndex >= documents.size()) {
            return Collections.emptyList();
        }

        return documents.subList(startIndex, endIndex).stream()
                .map(this::convertDocumentToDTO)
                .toList();
    }

    @Override
    @Transactional
    public ImportJourneyResultDTO importPassengerJourneysFromExcel(MultipartFile file, ImportPassengerJourneyRequest request) {
        try {
            // Save uploaded file temporarily
            File tempFile = File.createTempFile("passenger_journeys_import", ".xlsx");
            file.transferTo(tempFile);

            // Read Excel file
            List<Map<String, Object>> rows = ExcelFileUtils.readExcelAsMap(tempFile.getAbsolutePath());
            int totalRows = rows.size();

            if (totalRows == 0) {
                return new ImportJourneyResultDTO(0, 0, 0, List.of());
            }

            // Step 1: Import all components
            List<ImportPassengerRequest> passengerInfos = extractPassengerInfos(rows, request);
            ImportPassengerResultDTO passengerResult = passengerService.importPassengers(passengerInfos);

            List<ImportPlaceRequest> pickupPlaces = extractPickupPlaces(rows, request);
            ImportPlaceResultDTO pickupResult = placeService.importPlaces(pickupPlaces);

            List<ImportPlaceRequest> dropoffPlaces = extractDropoffPlaces(rows, request);
            ImportPlaceResultDTO dropoffResult = placeService.importPlaces(dropoffPlaces);

            // Step 2: Aggregate failed row indices
            Set<Integer> failedRowIndices = aggregateFailedRows(
                    passengerResult.errors(),
                    pickupResult.errors(),
                    dropoffResult.errors()
            );

            // Step 3: Build journeys for successful rows
            List<PassengerJourney> journeysToSave = buildJourneysForSuccessfulRows(
                    rows, request, passengerResult.passengers(),
                    pickupResult.places(), dropoffResult.places(), failedRowIndices
            );

            // Step 4: Save journeys with error handling
            List<ImportErrorDTO> journeyErrors = new ArrayList<>();
            List<PassengerJourney> savedJourneys = saveJourneysWithErrorHandling(
                    journeysToSave, journeyErrors, failedRowIndices
            );

            // Clean up
            tempFile.delete();

            // Step 5: Build final result
            return buildFinalResult(totalRows, passengerResult, pickupResult,
                    dropoffResult, savedJourneys, journeyErrors);

        } catch (Exception e) {
            log.error("Failed to import passenger journeys from Excel", e);
            throw new BusinessException(TripErrorCode.EXCEL_IMPORT_FAILED, e.getMessage());
        }
    }

    private Set<Integer> aggregateFailedRows(
            List<ImportErrorDTO> passengerErrors,
            List<ImportErrorDTO> pickupErrors,
            List<ImportErrorDTO> dropoffErrors) {

        Set<Integer> failedRows = new HashSet<>();

        passengerErrors.forEach(error -> failedRows.add(error.row()));
        pickupErrors.forEach(error -> failedRows.add(error.row()));
        dropoffErrors.forEach(error -> failedRows.add(error.row()));

        log.debug("Failed rows: {}", failedRows);
        return failedRows;
    }

    private List<PassengerJourney> buildJourneysForSuccessfulRows(
            List<Map<String, Object>> rows,
            ImportPassengerJourneyRequest request,
            List<PassengerDTO> importedPassengers,
            List<PlaceDTO> importedPickupPlaces,
            List<PlaceDTO> importedDropoffPlaces,
            Set<Integer> failedRowIndices) {

        List<PassengerJourney> entities = new ArrayList<>();
        int successfulIndex = 0; // Index in successful result lists

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            // Skip failed rows
            if (failedRowIndices.contains(rowIndex)) {
                log.debug("Skipping failed row: {}", rowIndex);
                continue;
            }

            try {
                Map<String, Object> row = rows.get(rowIndex);

                // Simple mapping: use successfulIndex to get data from successful lists
                if (successfulIndex < importedPassengers.size() &&
                        successfulIndex < importedPickupPlaces.size() &&
                        successfulIndex < importedDropoffPlaces.size()) {

                    PassengerDTO passenger = importedPassengers.get(successfulIndex);
                    PlaceDTO pickupPlace = importedPickupPlaces.get(successfulIndex);
                    PlaceDTO dropoffPlace = importedDropoffPlaces.get(successfulIndex);

                    // Parse journey-specific data from row
                    OffsetDateTime arrivalTime = parseArrivalTime(row, request);
                    Integer seatCount = parseSeatCount(row, request);
                    String journeyTypeStr = getValueIfHeaderExists(row, request.journeyType().toString());
                    JourneyType journeyType = JourneyType.valueOf(journeyTypeStr);

                    if (arrivalTime != null && seatCount != null) {
                        PassengerJourney journey = new PassengerJourney();
                        journey.setPassengerId(passenger.id());
                        journey.setPickupPlaceId(pickupPlace.id());
                        journey.setDropoffPlaceId(dropoffPlace.id());
                        journey.setMainStopArrivalTime(arrivalTime);
                        journey.setSeatCount(seatCount);
                        journey.setStatus(JourneyStatus.NOT_SCHEDULED);
                        journey.setJourneyType(journeyType);
                        entities.add(journey);
                        successfulIndex++;
                    } else {
                        log.warn("Invalid journey data at row {}: arrivalTime={}, seatCount={}",
                                rowIndex, arrivalTime, seatCount);
                    }
                } else {
                    log.warn("Insufficient successful data for row {}: passengers={}, pickups={}, dropoffs={}",
                            rowIndex, importedPassengers.size(), importedPickupPlaces.size(), importedDropoffPlaces.size());
                }

            } catch (Exception ex) {
                log.warn("Failed to build journey for row {}: {}", rowIndex, ex.getMessage());
                // This will be handled as journey error
            }
        }

        log.info("Built {} journeys from {} successful rows", entities.size(),
                rows.size() - failedRowIndices.size());
        return entities;
    }

    private OffsetDateTime parseArrivalTime(Map<String, Object> row, ImportPassengerJourneyRequest request) {
        try {
            String timeStr = getValueIfHeaderExists(row, request.latestStopArrivalTime());
            return timeStr != null ? OffsetDateTime.parse(timeStr) : null;
        } catch (Exception ex) {
            log.warn("Failed to parse arrival time: {}", ex.getMessage());
            return null;
        }
    }

    private Integer parseSeatCount(Map<String, Object> row, ImportPassengerJourneyRequest request) {
        try {
            String seatStr = getValueIfHeaderExists(row, request.seatCount());
            return seatStr != null ? (int) Double.parseDouble(seatStr) : null;
        } catch (Exception ex) {
            log.warn("Failed to parse seat count: {}", ex.getMessage());
            return null;
        }
    }

    private List<PassengerJourney> saveJourneysWithErrorHandling(
            List<PassengerJourney> journeys,
            List<ImportErrorDTO> journeyErrors,
            Set<Integer> failedRowIndices) {

        if (journeys.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // Bulk save
            List<PassengerJourney> savedJourneys = passengerJourneyRepository.saveAll(journeys);
            // Index all to Elasticsearch
            for (PassengerJourney saved : savedJourneys) {
                indexToElasticsearch(saved,
                        passengerService.getPassengerById(saved.getPassengerId()),
                        placeService.getPlace(saved.getPickupPlaceId()),
                        placeService.getPlace(saved.getDropoffPlaceId()));
            }
            log.info("Successfully saved {} journeys in bulk", savedJourneys.size());
            return savedJourneys;

        } catch (Exception ex) {
            log.warn("Bulk save failed, falling back to individual saves: {}", ex.getMessage());

            // Fallback: individual saves
            List<PassengerJourney> savedJourneys = new ArrayList<>();

            for (int i = 0; i < journeys.size(); i++) {
                PassengerJourney journey = journeys.get(i);
                try {
                    PassengerJourney saved = passengerJourneyRepository.save(journey);
                    savedJourneys.add(saved);
                    indexToElasticsearch(saved,
                            passengerService.getPassengerById(saved.getPassengerId()),
                            placeService.getPlace(saved.getPickupPlaceId()),
                            placeService.getPlace(saved.getDropoffPlaceId()));
                } catch (Exception innerEx) {
                    // Find original row index để report error chính xác
                    int originalRowIndex = findOriginalRowIndex(i, failedRowIndices);
                    journeyErrors.add(new ImportErrorDTO(originalRowIndex,
                            "Failed to save journey: " + innerEx.getMessage()));
                }
            }

            log.info("Saved {} journeys individually, {} failed",
                    savedJourneys.size(), journeyErrors.size());
            return savedJourneys;
        }
    }

    private int findOriginalRowIndex(int successfulJourneyIndex, Set<Integer> failedRowIndices) {
        // Calculate original row index by accounting for failed rows before this journey
        int originalIndex = successfulJourneyIndex;

        for (Integer failedIndex : failedRowIndices) {
            if (failedIndex <= originalIndex) {
                originalIndex++;
            }
        }

        return originalIndex;
    }

    private ImportJourneyResultDTO buildFinalResult(
            int totalRows,
            ImportPassengerResultDTO passengerResult,
            ImportPlaceResultDTO pickupResult,
            ImportPlaceResultDTO dropoffResult,
            List<PassengerJourney> savedJourneys,
            List<ImportErrorDTO> journeyErrors) {

        // Aggregate all errors
        journeyErrors.addAll(passengerResult.errors());
        journeyErrors.addAll(pickupResult.errors());
        journeyErrors.addAll(dropoffResult.errors());

        // Remove duplicate errors based on row number & merge messages & sort by row
        Map<Integer, String> errorMap = new HashMap<>();
        for (ImportErrorDTO error : journeyErrors) {
            errorMap.merge(error.row(), error.message(), (oldMsg, newMsg) -> oldMsg + "; " + newMsg);
        }
        List<ImportErrorDTO> uniqueSortedErrors = errorMap.entrySet().stream()
                .map(entry -> new ImportErrorDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(ImportErrorDTO::row))
                .toList();

        return new ImportJourneyResultDTO(
                totalRows,
                savedJourneys.size(),
                totalRows - savedJourneys.size(),
                uniqueSortedErrors
        );
    }

    // Updated extract methods với better error handling
    private List<ImportPassengerRequest> extractPassengerInfos(List<Map<String, Object>> rows, ImportPassengerJourneyRequest request) {
        return rows.stream().map(row -> {
            try {
                String email = getValueIfHeaderExists(row, request.email());
                String firstName = getValueIfHeaderExists(row, request.firstName());
                String lastName = getValueIfHeaderExists(row, request.lastName());
                String phoneNumber = getValueIfHeaderExists(row, request.phoneNumber());
                String zaloPhoneNumber = getValueIfHeaderExists(row, request.zaloPhoneNumber());

                ImportAccountRequest importAccountRequest = new ImportAccountRequest(
                        email, firstName, lastName, null, null, phoneNumber, zaloPhoneNumber, Set.of(RoleName.PASSENGER)
                );

                return new ImportPassengerRequest(importAccountRequest, 0, 0);
            } catch (Exception ex) {
                log.warn("Failed to extract passenger info from row: {}", ex.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private List<ImportPlaceRequest> extractPickupPlaces(List<Map<String, Object>> rows, ImportPassengerJourneyRequest request) {
        return rows.stream().map(row -> {
            try {
                String placeName = getValueIfHeaderExists(row, request.pickupPlaceName());
                String latStr = getValueIfHeaderExists(row, request.pickupLatitude());
                String lonStr = getValueIfHeaderExists(row, request.pickupLongitude());
                String address = getValueIfHeaderExists(row, request.pickupAddress());

                if (latStr != null && lonStr != null) {
                    Double latitude = Double.parseDouble(latStr);
                    Double longitude = Double.parseDouble(lonStr);
                    return new ImportPlaceRequest(placeName, latitude, longitude, address);
                }
                return null;
            } catch (Exception ex) {
                log.warn("Failed to extract pickup place from row: {}", ex.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private List<ImportPlaceRequest> extractDropoffPlaces(List<Map<String, Object>> rows, ImportPassengerJourneyRequest request) {
        return rows.stream().map(row -> {
            try {
                String placeName = getValueIfHeaderExists(row, request.dropoffPlaceName());
                String latStr = getValueIfHeaderExists(row, request.dropoffLatitude());
                String lonStr = getValueIfHeaderExists(row, request.dropoffLongitude());
                String address = getValueIfHeaderExists(row, request.dropoffAddress());

                if (latStr != null && lonStr != null) {
                    Double latitude = Double.parseDouble(latStr);
                    Double longitude = Double.parseDouble(lonStr);
                    return new ImportPlaceRequest(placeName, latitude, longitude, address);
                }
                return null;
            } catch (Exception ex) {
                log.warn("Failed to extract dropoff place from row: {}", ex.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private String getValueIfHeaderExists(Map<String, Object> row, String header) {
        if (header == null || header.isBlank()) return null; // header không có → bỏ qua
        Object value = row.get(header);
        return value != null ? value.toString() : null;
    }


    @Override
    @Transactional
    public PassengerJourneyDTO updateJourneyStatus(UUID journeyId, JourneyStatus status) {
        PassengerJourney journey = passengerJourneyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        journey.setStatus(status);

        // Update passenger statistics if journey is completed or cancelled
        if (status == JourneyStatus.COMPLETED) {
            passengerService.incrementCompletedTrips(journey.getPassengerId());
        } else if (status == JourneyStatus.CANCELED) {
            passengerService.incrementCancelledTrips(journey.getPassengerId());
        }

        PassengerJourney savedJourney = passengerJourneyRepository.save(journey);

        // Update Elasticsearch
        PassengerDTO passenger = passengerService.getPassengerById(savedJourney.getPassengerId());
        PlaceDTO pickupPlace = placeService.getPlace(savedJourney.getPickupPlaceId());
        PlaceDTO dropoffPlace = placeService.getPlace(savedJourney.getDropoffPlaceId());
        indexToElasticsearch(savedJourney, passenger, pickupPlace, dropoffPlace);

        return mapToDTO(savedJourney, passenger, pickupPlace, dropoffPlace);
    }

    @Override
    @Transactional
    public PassengerJourneyDTO setActualPickupTime(UUID journeyId, OffsetDateTime pickupTime) {
        PassengerJourney journey = passengerJourneyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        journey.setActualPickupTime(pickupTime);
        if (journey.getStatus() == JourneyStatus.SCHEDULED) {
            journey.setStatus(JourneyStatus.IN_PROGRESS);
        }

        PassengerJourney savedJourney = passengerJourneyRepository.save(journey);
        return mapToDTO(savedJourney);
    }

    @Override
    @Transactional
    public PassengerJourneyDTO setActualDropoffTime(UUID journeyId, OffsetDateTime dropoffTime) {
        PassengerJourney journey = passengerJourneyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        journey.setActualDropoffTime(dropoffTime);
        journey.setStatus(JourneyStatus.COMPLETED);

        // Update passenger completed trips count
        passengerService.incrementCompletedTrips(journey.getPassengerId());

        PassengerJourney savedJourney = passengerJourneyRepository.save(journey);
        return mapToDTO(savedJourney);
    }

    @Override
    public long countJourneysByStatus(JourneyStatus status) {
        return passengerJourneyRepository.countByStatus(status);
    }

    @Override
    public long countJourneysByPassenger(UUID passengerId) {
        return passengerJourneyRepository.countByPassengerId(passengerId);
    }

    @Transactional
    @Override
    public JourneyStatusData confirmPickup(ConfirmPickupRequest request) {
        PassengerJourney journey = passengerJourneyRepository.findByIdWithStops(request.passengerJourneyId())
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        if (journey.getStatus() != JourneyStatus.SCHEDULED) {
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_STATUS_INVALID);
        }

        // Lấy Stop mà hành động là PICKUP
        Stop pickupStop = journey.getStopJourneyMappings().stream()
                .filter(m -> m.getAction() == StopAction.PICKUP)
                .map(StopJourneyMapping::getStop)
                .findFirst()
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NO_STOPS_FOUND));

        Point stopPoint = Point.fromLngLat(pickupStop.getLongitude(), pickupStop.getLatitude());
        Point passengerPoint = Point.fromLngLat(request.longitude(), request.latitude());
        double distance = TurfMeasurement.distance(stopPoint, passengerPoint, "meters");

        if (distance > 50) {
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_PICKUP_TOO_FAR);
        }

        journey.setActualPickupTime(OffsetDateTime.now());
        journey.setStatus(JourneyStatus.IN_PROGRESS);
        passengerJourneyRepository.save(journey);

        return new JourneyStatusData(JourneyStatus.IN_PROGRESS);
    }

    @Transactional
    @Override
    public JourneyStatusData confirmDropoff(ConfirmDropoffRequest request) {
        PassengerJourney journey = passengerJourneyRepository.findByIdWithStops(request.passengerJourneyId())
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        if (journey.getStatus() != JourneyStatus.IN_PROGRESS) {
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_STATUS_INVALID);
        }

        // Lấy Stop mà hành động là DROPOFF
        Stop dropoffStop = journey.getStopJourneyMappings().stream()
                .filter(m -> m.getAction() == StopAction.DROPOFF)
                .map(StopJourneyMapping::getStop)
                .findFirst()
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NO_STOPS_FOUND));

        Point stopPoint = Point.fromLngLat(dropoffStop.getLongitude(), dropoffStop.getLatitude());
        Point passengerPoint = Point.fromLngLat(request.longitude(), request.latitude());
        double distance = TurfMeasurement.distance(stopPoint, passengerPoint, "meters");

        if (distance > 50) {
            throw new BusinessException(TripErrorCode.PASSENGER_JOURNEY_DROPOFF_TOO_FAR);
        }

        journey.setActualDropoffTime(OffsetDateTime.now());
        journey.setStatus(JourneyStatus.COMPLETED);
        passengerJourneyRepository.save(journey);

        // Cập nhật số chuyến hoàn thành cho hành khách
        passengerService.incrementCompletedTrips(journey.getPassengerId());

        return new JourneyStatusData(JourneyStatus.COMPLETED);
    }

    // Helper methods
    private PassengerJourneyDTO mapToDTO(PassengerJourney journey) {
        PassengerDTO passenger = passengerService.getPassengerById(journey.getPassengerId());
        PlaceDTO pickupPlace = placeService.getPlace(journey.getPickupPlaceId());
        PlaceDTO dropoffPlace = placeService.getPlace(journey.getDropoffPlaceId());

        return mapToDTO(journey, passenger, pickupPlace, dropoffPlace);
    }

    private PassengerJourneyDTO mapToDTO(PassengerJourney journey, PassengerDTO passenger, PlaceDTO pickupPlace, PlaceDTO dropoffPlace) {
        return new PassengerJourneyDTO(
                journey.getId(),
                passenger,
                pickupPlace,
                dropoffPlace,
                journey.getMainStopArrivalTime(),
                journey.getSeatCount(),
                journey.getJourneyType(),
                journey.getGeometry(),
                journey.getPlannedPickupTime(),
                journey.getActualPickupTime(),
                journey.getPlannedDropoffTime(),
                journey.getActualDropoffTime(),
                journey.getStatus()
        );
    }

    private void indexToElasticsearch(PassengerJourney journey, PassengerDTO passenger, PlaceDTO pickupPlace, PlaceDTO dropoffPlace) {
        try {
            PassengerJourneyDocument document = new PassengerJourneyDocument();
            document.setId(journey.getId());
            document.setPassengerId(journey.getPassengerId());
            document.setPassengerName(passenger.account().firstName() + " " + passenger.account().lastName());
            document.setPassengerEmail(passenger.account().email());
            document.setPickupPlaceId(journey.getPickupPlaceId());
            document.setPickupPlaceName(pickupPlace.name());
            document.setDropoffPlaceId(journey.getDropoffPlaceId());
            document.setDropoffPlaceName(dropoffPlace.name());
            document.setLastestStopArrivalTime(journey.getMainStopArrivalTime());
            document.setActualPickupTime(journey.getActualPickupTime());
            document.setActualDropoffTime(journey.getActualDropoffTime());
            document.setSeatCount(journey.getSeatCount());
            document.setStatus(journey.getStatus().name());

            passengerJourneyESRepository.save(document);
        } catch (Exception e) {
            log.warn("Failed to index passenger journey to Elasticsearch: {}", journey.getId(), e);
        }
    }

    private PassengerJourneyDTO convertDocumentToDTO(PassengerJourneyDocument document) {
        // This is a simplified conversion - in a real scenario, you might want to fetch full DTOs
        PassengerDTO passenger = passengerService.getPassengerById(document.getPassengerId());
        PlaceDTO pickupPlace = placeService.getPlace(document.getPickupPlaceId());
        PlaceDTO dropoffPlace = placeService.getPlace(document.getDropoffPlaceId());

        return new PassengerJourneyDTO(
                document.getId(),
                passenger,
                pickupPlace,
                dropoffPlace,
                document.getLastestStopArrivalTime(),
                document.getSeatCount(),
                null,
                null,
                null,
                document.getActualPickupTime(),
                null,
                document.getActualDropoffTime(),
                JourneyStatus.valueOf(document.getStatus())
        );
    }


}

package vn.edu.fpt.transitlink.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.transitlink.identity.dto.PassengerDTO;
import vn.edu.fpt.transitlink.identity.enumeration.RoleName;
import vn.edu.fpt.transitlink.identity.request.ImportAccountRequest;
import vn.edu.fpt.transitlink.identity.request.ImportPassengerRequest;
import vn.edu.fpt.transitlink.identity.service.PassengerService;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.request.ImportPlaceRequest;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;
import vn.edu.fpt.transitlink.shared.util.ExcelFileUtils;
import vn.edu.fpt.transitlink.trip.dto.ImportResultDTO;
import vn.edu.fpt.transitlink.trip.dto.PassengerJourneyDTO;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourney;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourneyDocument;
import vn.edu.fpt.transitlink.trip.enumeration.JourneyStatus;
import vn.edu.fpt.transitlink.trip.exception.TripErrorCode;
import vn.edu.fpt.transitlink.trip.repository.PassengerJourneyESRepository;
import vn.edu.fpt.transitlink.trip.repository.PassengerJourneyRepository;
import vn.edu.fpt.transitlink.trip.request.CreatePassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.ImportPassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.SearchPassengerJourneyRequest;
import vn.edu.fpt.transitlink.trip.request.UpdatePassengerJourneyRequest;
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
            journey.setLastestStopArrivalTime(request.lastestStopArrivalTime());
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

            if (request.routeId() != null) {
                journey.setRouteId(request.routeId());
            }

            if (request.lastestStopArrivalTime() != null) {
                journey.setLastestStopArrivalTime(request.lastestStopArrivalTime());
            }

            if (request.seatCount() != null) {
                journey.setSeatCount(request.seatCount());
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
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastestStopArrivalTime").ascending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByLastestStopArrivalTimeBetween(startDate, endDate, pageable);

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
    public List<PassengerJourneyDTO> getCurrentPassengerJourneys(UUID passengerId, int page, int size) {
        List<JourneyStatus> currentStatuses = Arrays.asList(
                JourneyStatus.NOT_SCHEDULED,
                JourneyStatus.SCHEDULED,
                JourneyStatus.IN_PROGRESS
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PassengerJourney> journeyPage = passengerJourneyRepository.findByPassengerIdAndStatusIn(passengerId, currentStatuses, pageable);

        return journeyPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();
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
    public ImportResultDTO importPassengerJourneysFromExcel(MultipartFile file) {
        List<ImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        List<PassengerJourneyDTO> successfulJourneys = new ArrayList<>();
        int totalRows = 0;
        int successfulImports = 0;
        int failedImports = 0;

        try {
            // Save uploaded file temporarily
            File tempFile = File.createTempFile("passenger_journeys_import", ".xlsx");
            file.transferTo(tempFile);

            // Read Excel file
            List<Map<String, Object>> rows = ExcelFileUtils.readExcelAsMap(tempFile.getAbsolutePath());

            List<ImportPassengerRequest> passengerInfos = extractPassengerInfos(rows);
            List<PassengerDTO> importedPassengers =passengerService.importPassengers(passengerInfos);
            List<ImportPlaceRequest> pickupPlaces = extractPickupPlaces(rows);
            List<PlaceDTO> importedPickupPlaces = placeService.importPlaces(pickupPlaces);
            List<ImportPlaceRequest> dropoffPlaces = extractDropoffPlaces(rows);
            List<PlaceDTO> importedDropoffPlaces = placeService.importPlaces(dropoffPlaces);

            List<PassengerJourney> entities = buildBulkInsertEntity(
                    importedPassengers, importedPickupPlaces, importedDropoffPlaces, rows
            );
            passengerJourneyRepository.saveAll(entities);

            // Clean up temporary file
            tempFile.delete();

        } catch (Exception e) {
            log.error("Failed to import passenger journeys from Excel", e);
            throw new BusinessException(TripErrorCode.EXCEL_IMPORT_FAILED, e.getMessage());
        }

        return new ImportResultDTO(totalRows, successfulImports, failedImports, successfulJourneys, errors);
    }

    private List<ImportPassengerRequest> extractPassengerInfos(List<Map<String, Object>> rows) {
        List<ImportPassengerRequest> passengerInfos = new ArrayList<>();

        rows.stream().forEach(row -> {
            String email = row.get("Email").toString();
            String firstName = row.get("First Name").toString();
            String lastName = row.get("Last Name").toString();
            String phoneNumber = row.get("Phone Number").toString();
            String zaloPhoneNumber = row.get("Zalo Phone Number").toString();

            ImportAccountRequest importAccountRequest = new ImportAccountRequest(
                    email, firstName, lastName, null, null, phoneNumber, zaloPhoneNumber, Set.of(RoleName.PASSENGER)
            );

            passengerInfos.add(new ImportPassengerRequest(importAccountRequest, 0, 0));
        });

        return passengerInfos;
    }

    private List<ImportPlaceRequest> extractPickupPlaces(List<Map<String, Object>> rows) {
        List<ImportPlaceRequest> pickupPlaces = new ArrayList<>();
        rows.stream().forEach(row -> {
            String placeName = row.get("Pickup Place Name").toString();
            Double latitude = Double.parseDouble(row.get("Pickup Latitude").toString());
            Double longitude = Double.parseDouble(row.get("Pickup Longitude").toString());
            String address = row.get("Pickup Address").toString();
            pickupPlaces.add(new ImportPlaceRequest(placeName, latitude, longitude, address));
        });
        return pickupPlaces;
    }

    private List<ImportPlaceRequest> extractDropoffPlaces(List<Map<String, Object>> rows) {
        List<ImportPlaceRequest> dropoffPlaces = new ArrayList<>();
        rows.stream().forEach(row -> {
            String placeName = row.get("Dropoff Place Name").toString();
            Double latitude = Double.parseDouble(row.get("Dropoff Latitude").toString());
            Double longitude = Double.parseDouble(row.get("Dropoff Longitude").toString());
            String address = row.get("Dropoff Address").toString();
            dropoffPlaces.add(new ImportPlaceRequest(placeName, latitude, longitude, address));
        });
        return dropoffPlaces;
    }

    private List<PassengerJourney> buildBulkInsertEntity(List<PassengerDTO> importedPassengers,
                                                                           List<PlaceDTO> importedPickupPlaces,
                                                                           List<PlaceDTO> importedDropoffPlaces,
                                                                           List<Map<String, Object>> rows) {
        List<PassengerJourney> entities = new ArrayList<>();

        int totalRows = rows.size();
        for (int i = 0; i < totalRows; i++) {
            Map<String, Object> row = rows.get(i);
            PassengerDTO passenger = importedPassengers.get(i);
            PlaceDTO pickupPlace = importedPickupPlaces.get(i);
            PlaceDTO dropoffPlace = importedDropoffPlaces.get(i);
            OffsetDateTime arrivalTime = OffsetDateTime.parse(row.get("Latest Stop Arrival Time").toString());
            Integer seatCount = Integer.parseInt(row.get("Seat Count").toString());

            PassengerJourney journey = new PassengerJourney();
            journey.setPassengerId(passenger.id());
            journey.setPickupPlaceId(pickupPlace.id());
            journey.setDropoffPlaceId(dropoffPlace.id());
            journey.setLastestStopArrivalTime(arrivalTime);
            journey.setSeatCount(seatCount);
            journey.setStatus(JourneyStatus.NOT_SCHEDULED);

            entities.add(journey);

        }
        return entities;
    }


    @Override
    @Transactional
    public PassengerJourneyDTO assignRoute(UUID journeyId, UUID routeId) {
        PassengerJourney journey = passengerJourneyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(TripErrorCode.PASSENGER_JOURNEY_NOT_FOUND));

        journey.setRouteId(routeId);
        journey.setStatus(JourneyStatus.SCHEDULED);

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
    public List<PassengerJourneyDTO> getJourneysForRoute(UUID routeId) {
        List<PassengerJourney> journeys = passengerJourneyRepository.findByRouteId(routeId);
        return journeys.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public long countJourneysByStatus(JourneyStatus status) {
        return passengerJourneyRepository.countByStatus(status);
    }

    @Override
    public long countJourneysByPassenger(UUID passengerId) {
        return passengerJourneyRepository.countByPassengerId(passengerId);
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
                journey.getRouteId(),
                journey.getLastestStopArrivalTime(),
                journey.getActualPickupTime(),
                journey.getActualDropoffTime(),
                journey.getSeatCount(),
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
            document.setRouteId(journey.getRouteId());
            document.setLastestStopArrivalTime(journey.getLastestStopArrivalTime());
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
                document.getRouteId(),
                document.getLastestStopArrivalTime(),
                document.getActualPickupTime(),
                document.getActualDropoffTime(),
                document.getSeatCount(),
                JourneyStatus.valueOf(document.getStatus())
        );
    }


}

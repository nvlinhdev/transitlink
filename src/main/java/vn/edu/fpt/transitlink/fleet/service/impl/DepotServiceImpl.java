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
import vn.edu.fpt.transitlink.fleet.entity.Depot;
import vn.edu.fpt.transitlink.fleet.exception.FleetErrorCode;
import vn.edu.fpt.transitlink.fleet.repository.DepotRepository;
import vn.edu.fpt.transitlink.fleet.request.CreateDepotRequest;
import vn.edu.fpt.transitlink.fleet.request.UpdateDepotRequest;
import vn.edu.fpt.transitlink.fleet.service.DepotService;
import vn.edu.fpt.transitlink.location.dto.PlaceDTO;
import vn.edu.fpt.transitlink.location.service.PlaceService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DepotServiceImpl implements DepotService {
    private final DepotRepository depotRepository;
    private final PlaceService placeService;

    @Transactional
    @Caching(
            put = {@CachePut(value = "depotsById", key = "#result.id()")},
            evict = {@CacheEvict(value = "depotsPage", allEntries = true)}
    )
    @Override
    public DepotDTO createDepot(CreateDepotRequest request) {
        PlaceDTO place;
        if (request.placeId() == null) {
            PlaceDTO placeDTO = new PlaceDTO(
                    UUID.randomUUID(),
                    request.placeName(),
                    request.placeLatitude(),
                    request.placeLongitude(),
                    request.placeAddress());
            place = placeService.savePlace(placeDTO);
        } else {
            place = placeService.getPlace(request.placeId());
            if (place == null) {
                throw new BusinessException(FleetErrorCode.DEPOT_PLACE_NOT_FOUND);
            }
        }

        Depot depot = new Depot();
        depot.setName(request.name());
        depot.setPlaceId(place.id());

        Depot savedDepot = depotRepository.save(depot);

        return new DepotDTO(
                savedDepot.getId(),
                savedDepot.getName(),
                place
        );
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "depotsById", key = "#id")},
            evict = {@CacheEvict(value = "depotsPage", allEntries = true)}
    )
    @Override
    public DepotDTO updateDepot(UUID id, UpdateDepotRequest request) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.DEPOT_NOT_FOUND));

        PlaceDTO place;
        if (request.placeId() == null &&
                (request.placeName() != null || request.placeLatitude() != null ||
                        request.placeLongitude() != null || request.placeAddress() != null)) {
            // Create new place if all necessary details are provided
            PlaceDTO placeDTO = new PlaceDTO(
                    UUID.randomUUID(),
                    request.placeName(),
                    request.placeLatitude(),
                    request.placeLongitude(),
                    request.placeAddress());
            place = placeService.savePlace(placeDTO);
            depot.setPlaceId(place.id());
        } else if (request.placeId() != null) {
            // Use existing place
            place = placeService.getPlace(request.placeId());
            if (place == null) {
                throw new BusinessException(FleetErrorCode.DEPOT_PLACE_NOT_FOUND);
            }
            depot.setPlaceId(place.id());
        } else {
            // Keep existing place
            place = placeService.getPlace(depot.getPlaceId());
            if (place == null) {
                throw new BusinessException(FleetErrorCode.DEPOT_PLACE_NOT_FOUND);
            }
        }

        if (request.name() != null) {
            depot.setName(request.name());
        }

        Depot savedDepot = depotRepository.save(depot);

        return new DepotDTO(
                savedDepot.getId(),
                savedDepot.getName(),
                place
        );
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "depotsById", key = "#id"),
                    @CacheEvict(value = "depotsPage", allEntries = true),
                    @CacheEvict(value = "deletedDepotsPage", allEntries = true) // Thêm dòng này để xóa cache của danh sách depot đã bị xóa
            }
    )
    @Override
    public DepotDTO deleteDepot(UUID id, UUID deletedBy) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.DEPOT_NOT_FOUND));

        if (depot.isDeleted()) {
            throw new BusinessException(FleetErrorCode.DEPOT_ALREADY_DELETED);
        }

        // Get place data before deleting the depot
        PlaceDTO place = placeService.getPlace(depot.getPlaceId());
        if (place == null) {
            throw new BusinessException(FleetErrorCode.DEPOT_PLACE_NOT_FOUND);
        }

        depot.softDelete(deletedBy);
        depotRepository.save(depot);

        // Return depot data for potential restoration
        return new DepotDTO(
                depot.getId(),
                depot.getName(),
                place
        );
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "depotsById", key = "#id")},
            evict = {
                    @CacheEvict(value = "depotsPage", allEntries = true),
                    @CacheEvict(value = "deletedDepotsPage", allEntries = true)
            }
    )
    @Override
    public DepotDTO restoreDepot(UUID id) {
        Depot depot = depotRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.DEPOT_NOT_FOUND));

        if (!depot.isDeleted()) {
            throw new BusinessException(FleetErrorCode.DEPOT_NOT_DELETED);
        }

        depot.restore();
        Depot savedDepot = depotRepository.save(depot);

        PlaceDTO place = placeService.getPlace(savedDepot.getPlaceId());
        if (place == null) {
            throw new BusinessException(FleetErrorCode.DEPOT_PLACE_NOT_FOUND);
        }

        return new DepotDTO(
                savedDepot.getId(),
                savedDepot.getName(),
                place
        );
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "depotsById", key = "#id"),
                    @CacheEvict(value = "depotsPage", allEntries = true),
                    @CacheEvict(value = "deletedDepotsPage", allEntries = true)
            }
    )
    @Override
    public void hardDeleteDepot(UUID id) {
        Depot depot = depotRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.DEPOT_NOT_FOUND));

        depotRepository.delete(depot);
    }

    @Cacheable(value = "depotsById", key = "#id")
    @Override
    public DepotDTO getDepot(UUID id) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FleetErrorCode.DEPOT_NOT_FOUND));

        PlaceDTO place = placeService.getPlace(depot.getPlaceId());
        if (place == null) {
            throw new BusinessException(FleetErrorCode.DEPOT_PLACE_NOT_FOUND);
        }

        return new DepotDTO(
                depot.getId(),
                depot.getName(),
                place
        );
    }

    @Cacheable(value = "depotsPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<DepotDTO> getDepots(int page, int size) {
        List<Depot> depots = depotRepository.findAll(PageRequest.of(page, size)).getContent();

        return depots.stream()
                .map(depot -> {
                    PlaceDTO place = placeService.getPlace(depot.getPlaceId());
                    return new DepotDTO(
                            depot.getId(),
                            depot.getName(),
                            place
                    );
                }).toList();
    }

    @Cacheable(value = "deletedDepotsPage", key = "'page:' + #page + ':size:' + #size")
    @Override
    public List<DepotDTO> getDeletedDepots(int page, int size) {
        List<Depot> deletedDepots = depotRepository.findAllDeleted(PageRequest.of(page, size)).getContent();

        return deletedDepots.stream()
                .map(depot -> {
                    PlaceDTO place = placeService.getPlace(depot.getPlaceId());
                    return new DepotDTO(
                            depot.getId(),
                            depot.getName(),
                            place
                    );
                }).toList();
    }

    @Override
    public long countDepots() {
        return depotRepository.count();
    }

    @Override
    public long countDeletedDepots() {
        return depotRepository.countDeleted();
    }
}

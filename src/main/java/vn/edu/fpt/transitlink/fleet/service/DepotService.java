package vn.edu.fpt.transitlink.fleet.service;

import vn.edu.fpt.transitlink.fleet.dto.DepotDTO;
import vn.edu.fpt.transitlink.fleet.request.CreateDepotRequest;
import vn.edu.fpt.transitlink.fleet.request.UpdateDepotRequest;

import java.util.List;
import java.util.UUID;

public interface DepotService {
    DepotDTO createDepot(CreateDepotRequest request);
    DepotDTO updateDepot(UUID id, UpdateDepotRequest request);
    DepotDTO deleteDepot(UUID id, UUID deletedBy);
    DepotDTO restoreDepot(UUID id);
    void hardDeleteDepot(UUID id);
    DepotDTO getDepot(UUID id);
    List<DepotDTO> getDepots(int page, int size);
    long countDepots();
    List<DepotDTO> getDeletedDepots(int page, int size);
    long countDeletedDepots();
}

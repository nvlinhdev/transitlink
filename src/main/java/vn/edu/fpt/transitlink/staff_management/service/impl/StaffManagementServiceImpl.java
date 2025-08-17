package vn.edu.fpt.transitlink.staff_management.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.staff_management.dto.StaffManagementDTO;
import vn.edu.fpt.transitlink.staff_management.mapper.StaffManagementMapper;
import vn.edu.fpt.transitlink.staff_management.repository.StaffManagementRepository;
import vn.edu.fpt.transitlink.staff_management.service.StaffManagementService;

import java.util.UUID;

@Service
public class StaffManagementServiceImpl implements StaffManagementService {

    private final StaffManagementMapper mapper;

    private final StaffManagementRepository staffManagementRepository;


    public StaffManagementServiceImpl(StaffManagementMapper mapper, StaffManagementRepository staffManagementRepository) {
        this.mapper = StaffManagementMapper.INSTANCE;
        this.staffManagementRepository = staffManagementRepository;
    }

    @Override
    public StaffManagementDTO createStaffAccount(StaffManagementDTO staffManagementData) {
        return null;
    }

    @Override
    public StaffManagementDTO deleteStaffAccount(UUID staffAccountId) {
        return null;
    }
}

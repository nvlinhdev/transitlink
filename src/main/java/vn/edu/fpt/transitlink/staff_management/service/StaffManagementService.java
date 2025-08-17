package vn.edu.fpt.transitlink.staff_management.service;

import vn.edu.fpt.transitlink.staff_management.dto.StaffManagementDTO;

import java.util.UUID;

public interface StaffManagementService {

    StaffManagementDTO createStaffAccount(StaffManagementDTO staffManagementData);
    StaffManagementDTO deleteStaffAccount(UUID staffAccountId);
}

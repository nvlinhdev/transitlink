package vn.edu.fpt.transitlink.staff_management.dto;

import java.util.UUID;

public record StaffManagementDTO() {
     UUID id;
     String username;
     String role;
     String status;
}

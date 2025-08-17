package vn.edu.fpt.transitlink.staff_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.staff_management.dto.StaffManagementDTO;
import vn.edu.fpt.transitlink.staff_management.entity.StaffAccount;

@Mapper(componentModel = "spring")
public interface StaffManagementMapper {

    public static final StaffManagementMapper INSTANCE = Mappers.getMapper(StaffManagementMapper.class);

    public StaffManagementDTO toDTO(StaffAccount staffAccount);
}

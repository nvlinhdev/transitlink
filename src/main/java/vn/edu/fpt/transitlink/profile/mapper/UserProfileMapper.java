package vn.edu.fpt.transitlink.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.profile.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);
    UserProfileDTO toResponse(UserProfile profile);
}


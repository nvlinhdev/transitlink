package vn.edu.fpt.transitlink.navigation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.navigation.dto.NavigationDTO;
import vn.edu.fpt.transitlink.navigation.entity.NavigationData;

@Mapper(componentModel = "spring")
public interface NavigationMapper {
    public static final NavigationMapper INSTANCE = Mappers.getMapper(NavigationMapper.class);

    public NavigationDTO toDTO(NavigationData navigationData);

}

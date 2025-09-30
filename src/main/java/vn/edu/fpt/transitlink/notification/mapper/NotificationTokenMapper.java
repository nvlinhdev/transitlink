package vn.edu.fpt.transitlink.notification.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.notification.dto.NotificationTokenDTO;
import vn.edu.fpt.transitlink.notification.entity.NotificationToken;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationTokenMapper {
    NotificationTokenDTO toDTO(NotificationToken entity);
    NotificationToken toEntity(NotificationTokenDTO dto);
    List<NotificationTokenDTO> toDTOList(List<NotificationToken> tokens);
}

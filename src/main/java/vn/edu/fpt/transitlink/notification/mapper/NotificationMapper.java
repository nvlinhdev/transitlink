package vn.edu.fpt.transitlink.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    public static final NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    public NotificationDTO toDTO(Notification notification);
}

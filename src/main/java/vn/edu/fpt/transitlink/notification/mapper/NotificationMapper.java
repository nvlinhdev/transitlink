package vn.edu.fpt.transitlink.notification.mapper;

import org.mapstruct.Mapper;
import vn.edu.fpt.transitlink.notification.dto.NotificationDTO;
import vn.edu.fpt.transitlink.notification.entity.Notification;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDTO toDTO(Notification notification);
    Notification toEntity(NotificationDTO notificationDTO);
    List<NotificationDTO> toDTOList(List<Notification> notifications);
}

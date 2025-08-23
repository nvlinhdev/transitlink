package vn.edu.fpt.transitlink.notification.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.notification.mapper.NotificationMapper;
import vn.edu.fpt.transitlink.notification.repository.NotificationRepository;
import vn.edu.fpt.transitlink.notification.service.NoficationService;

@Service
public class NotificationServiceImpl implements NoficationService {
    private final NotificationMapper mapper;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationMapper mapper, NotificationRepository notificationRepository) {
        this.mapper = NotificationMapper.INSTANCE;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public boolean registerNotification(String pushToken, String deviceType, String deviceId) {
        return false;
    }

    @Override
    public boolean unregisterNotification(String pushToken, String deviceType, String deviceId) {
        return false;
    }
}

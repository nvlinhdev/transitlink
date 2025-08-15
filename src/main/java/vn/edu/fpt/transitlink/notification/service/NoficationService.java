package vn.edu.fpt.transitlink.notification.service;

public interface NoficationService {
    public boolean registerNotification(String pushToken, String deviceType, String deviceId);
    public boolean unregisterNotification(String pushToken, String deviceType, String deviceId);
}

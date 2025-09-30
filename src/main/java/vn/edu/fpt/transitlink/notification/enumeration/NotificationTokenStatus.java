package vn.edu.fpt.transitlink.notification.enumeration;

public enum NotificationTokenStatus {
    ACTIVE,     // user đang login, token hợp lệ
    LOGGED_OUT, // user đã logout
    INVALID     // token không còn hợp lệ (app uninstall, FCM reject)
}

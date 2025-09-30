package vn.edu.fpt.transitlink.identity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    ACCOUNT_CREATED("account-created-notification", "Tài khoản TransitLink của bạn đã được tạo"),
    ACCOUNT_UPDATED("account-updated-notification", "Thông tin tài khoản TransitLink đã được cập nhật"),
    ACCOUNT_DELETED("account-deleted-notification", "Tài khoản TransitLink đã bị vô hiệu hóa"),
    ACCOUNT_RESTORED("account-restored-notification", "Tài khoản TransitLink đã được khôi phục");

    private final String emailTemplate;
    private final String emailSubject;
}

@ApplicationModule(
        id = "notification",
        displayName = "Notification Module",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared"
        }
)
package vn.edu.fpt.transitlink.notification;

import org.springframework.modulith.ApplicationModule;
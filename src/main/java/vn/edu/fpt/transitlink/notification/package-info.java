@ApplicationModule(
        id = "notification",
        displayName = "Notification",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
                "firebase"
        }
)
package vn.edu.fpt.transitlink.notification;

import org.springframework.modulith.ApplicationModule;
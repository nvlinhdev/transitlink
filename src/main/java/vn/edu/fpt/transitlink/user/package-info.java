@ApplicationModule(
        id = "user",
        displayName = "User",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
                "auth",
        }
)


package vn.edu.fpt.transitlink.user;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        id = "profile",
        displayName = "Profile",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared"
        }
)
package vn.edu.fpt.transitlink.profile;

import org.springframework.modulith.ApplicationModule;
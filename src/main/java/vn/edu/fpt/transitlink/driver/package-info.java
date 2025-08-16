@ApplicationModule(
        id = "driver",
        displayName = "Driver",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.driver;

import org.springframework.modulith.ApplicationModule;
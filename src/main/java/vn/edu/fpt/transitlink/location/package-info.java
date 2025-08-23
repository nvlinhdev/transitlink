@ApplicationModule(
        id = "location",
        displayName = "Location",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.location;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        id = "trip",
        displayName = "Trip",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)
package vn.edu.fpt.transitlink.trip;

import org.springframework.modulith.ApplicationModule;
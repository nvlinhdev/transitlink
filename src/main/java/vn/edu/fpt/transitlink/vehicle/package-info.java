@ApplicationModule(
        id = "vehicle",
        displayName = "Vehicle",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.vehicle;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        id = "passenger",
        displayName = "Passenger",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.passenger;

import org.springframework.modulith.ApplicationModule;
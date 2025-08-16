@ApplicationModule(
        id = "route",
        displayName = "Route",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.route;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        id = "fleet",
        displayName = "Fleet Module",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
                "location::service",
                "location::dto",
        }
)

package vn.edu.fpt.transitlink.fleet;

import org.springframework.modulith.ApplicationModule;
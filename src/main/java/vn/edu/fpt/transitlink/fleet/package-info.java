@ApplicationModule(
        id = "fleet",
        displayName = "Fleet",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.fleet;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        id = "monitoring",
        displayName = "Monitoring",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)
package vn.edu.fpt.transitlink.monitoring;

import org.springframework.modulith.ApplicationModule;
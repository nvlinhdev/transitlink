@ApplicationModule(
        id = "reporting",
        displayName = "Reporting",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.reporting;

import org.springframework.modulith.ApplicationModule;
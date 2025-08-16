@ApplicationModule(
        id = "schedule",
        displayName = "Schedule",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)

package vn.edu.fpt.transitlink.schedule;

import org.springframework.modulith.ApplicationModule;

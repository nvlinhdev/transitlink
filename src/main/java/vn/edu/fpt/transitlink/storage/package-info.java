@ApplicationModule(
        id = "storage",
        displayName = "Storage Module",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared"
        }
)
package vn.edu.fpt.transitlink.storage;

import org.springframework.modulith.ApplicationModule;
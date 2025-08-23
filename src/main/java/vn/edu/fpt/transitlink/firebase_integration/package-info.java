@ApplicationModule(
        id = "firebase",
        displayName = "Firebase",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
                "profile"
        }
)
package vn.edu.fpt.transitlink.firebase_integration;

import org.springframework.modulith.ApplicationModule;
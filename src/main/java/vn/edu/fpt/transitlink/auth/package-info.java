@ApplicationModule(
        id = "auth",
        displayName =  "Authentication",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)
package vn.edu.fpt.transitlink.auth;

import org.springframework.modulith.ApplicationModule;
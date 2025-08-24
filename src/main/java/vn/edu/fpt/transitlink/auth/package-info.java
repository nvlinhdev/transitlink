@ApplicationModule(
        id = "auth",
        displayName =  "Authentication Module",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
                "mail-sender::service",
                "mail-sender::dto",
        }
)
package vn.edu.fpt.transitlink.auth;

import org.springframework.modulith.ApplicationModule;
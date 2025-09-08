@ApplicationModule(
        id = "identity",
        displayName =  "Identity Module",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
                "mail-sender::service",
                "mail-sender::dto",
                "fleet::dto",
                "fleet::service",
        }
)
package vn.edu.fpt.transitlink.identity;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        id = "trip",
        displayName = "Trip Module",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {"shared", "location::dto", "location::service", "identity::dto", "identity::service", "identity::request", "location :: request", "identity :: enumeration"}
)
package vn.edu.fpt.transitlink.trip;

import org.springframework.modulith.ApplicationModule;
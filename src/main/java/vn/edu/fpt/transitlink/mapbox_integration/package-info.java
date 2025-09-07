@ApplicationModule(
        displayName = "Mapbox Integration Module",
        allowedDependencies = {
                "location::spi",
                "location::dto",
        })
package vn.edu.fpt.transitlink.mapbox_integration;

import org.springframework.modulith.ApplicationModule;
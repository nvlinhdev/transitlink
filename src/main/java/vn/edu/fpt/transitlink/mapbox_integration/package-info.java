@ApplicationModule(
        displayName = "Mapbox Integration Module",
        allowedDependencies = {"location::spi", "location::dto", "trip::dto", "fleet::dto", "trip :: enumeration", "fleet", "shared", "trip", "trip :: spi", "fleet :: enumeration"})
package vn.edu.fpt.transitlink.mapbox_integration;

import org.springframework.modulith.ApplicationModule;
@ApplicationModule(
        displayName = "HERE Integration Module",
        allowedDependencies = {"trip::dto", "fleet::dto", "location::dto", "trip::enumeration", "identity :: dto"})
package vn.edu.fpt.transitlink.here_integration;

import org.springframework.modulith.ApplicationModule;
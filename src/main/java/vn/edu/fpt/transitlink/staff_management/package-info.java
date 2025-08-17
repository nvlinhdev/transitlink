@ApplicationModule(
        id = "staff_management",
        displayName = "Staff Management",
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared",
        }
)
package vn.edu.fpt.transitlink.staff_management;

import org.springframework.modulith.ApplicationModule;
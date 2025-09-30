package vn.edu.fpt.transitlink.identity.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleName {
    MANAGER,
    DISPATCHER,
    TICKET_SELLER,
    DRIVER,
    PASSENGER;

    @JsonCreator
    public static RoleName from(String value) {
        return value == null ? null : RoleName.valueOf(value.toUpperCase());
    }
}

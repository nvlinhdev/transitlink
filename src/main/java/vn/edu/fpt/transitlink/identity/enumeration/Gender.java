package vn.edu.fpt.transitlink.identity.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    @JsonCreator
    public static Gender from(String value) {
        return value == null ? null : Gender.valueOf(value.toUpperCase());
    }
}

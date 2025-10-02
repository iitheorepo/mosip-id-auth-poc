package io.mosip.authentication.internal.service.dto;

public enum EventType {
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    ACCESS("ACCESS"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    AUTHENTICATION("AUTHENTICATION"),
    AUTHORIZATION("AUTHORIZATION"),
    OTHER("OTHER");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

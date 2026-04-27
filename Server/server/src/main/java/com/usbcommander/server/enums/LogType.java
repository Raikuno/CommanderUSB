package com.usbcommander.server.enums;

public enum LogType {
    INFO(1001, "Information", "Information log. No weird activity found"),
    CONFIG_MOD(1002, "Configuration Modified", "Someone tried to change the configuration of the application saved on the registry"),
    REGISTRY_MOD(1003, "Registry Modified", "Someone tried to modify the values related to usb drive mounting in the registry"),
    INCOHERENT(1004, "Incoherent State", "An incoherence was found between the expected values and the ones found in the registry"),
    MEMORY_CONN(1005, "Memory Connection Issue", "An usb drive seems to have been connected to the machine"),
    CONNECTION(1006, "Connection Issue", "A log couldnt be sent because of a connection issue"),
    ERROR(1007, "Application Error", "An error occurred in the application");

    private final int code;
    private final String label;
    private final String description;

    LogType(int code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }

    public static LogType fromCode(int code) {
        for (LogType t : values()) {
            if (t.code == code) return t;
        }
        return null;
    }
}

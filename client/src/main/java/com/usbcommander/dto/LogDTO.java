package com.usbcommander.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class LogDTO{
    
    private int usbValue;

    private boolean usbAllowed;

    private List<Map<String,String>> usbList;

    private LocalDateTime creationDate;

    private LocalDateTime lastModificationDate;

    private String description;

    public LogDTO(int usbValue, boolean usbAllowed, List<Map<String, String>> usbList, LocalDateTime creationDate,
            LocalDateTime lastModificationDate) {
        this.usbValue = usbValue;
        this.usbAllowed = usbAllowed;
        this.usbList = usbList;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public int getUsbValue() {
        return usbValue;
    }

    public boolean isUsbAllowed() {
        return usbAllowed;
    }

    public List<Map<String, String>> getUsbList() {
        return usbList;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setUsbValue(int usbValue) {
        this.usbValue = usbValue;
    }

    public void setUsbAllowed(boolean usbAllowed) {
        this.usbAllowed = usbAllowed;
    }

    public void setUsbList(List<Map<String, String>> usbList) {
        this.usbList = usbList;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastModificationDate(LocalDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    
}

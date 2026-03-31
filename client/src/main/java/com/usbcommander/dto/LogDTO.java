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

    private String errorMessage;

    private int code;

    public LogDTO(int usbValue, boolean usbAllowed, List<Map<String, String>> usbList, LocalDateTime creationDate,
            int code) {
        this.usbValue = usbValue;
        this.usbAllowed = usbAllowed;
        this.usbList = usbList;
        this.creationDate = creationDate;
        this.code = code;
    }

    public LogDTO(int code, String errorMessage, LocalDateTime creationDate){
        this.code = code;
        this.errorMessage = errorMessage;
        this.creationDate = creationDate;
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

    public int getCode() {
        return code;
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

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
}

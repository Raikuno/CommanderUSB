package com.usbcommander.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Clase usada únicamanete como DTO (Data transfer object) para ser enviada y recibida en comunicaciones entre el cliente y el servidor, empleada para que el cliente pueda enviar la información de los registros al servidor
 */
public class LogDTO{
    
    private int usbValue;

    private boolean usbAllowed;

    private List<Map<String,String>> usbList;

    private LocalDateTime creationDate;

    private String errorMessage;

    private int code;

    public LogDTO(){}

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

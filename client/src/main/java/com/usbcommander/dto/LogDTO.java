package com.usbcommander.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LogDTO implements Serializable{
    
    @SuppressWarnings("unused")
    private int usbValue;

    @SuppressWarnings("unused")
    private boolean usbAllowed;

    @SuppressWarnings("unused")
    private String[] usbList;

    @SuppressWarnings("unused")
    private LocalDateTime creationDate;

    @SuppressWarnings("unused")
    private LocalDateTime lastModificationDate;

    @SuppressWarnings("unused")
    private String description;
}

package com.usbcommander.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LogDTO implements Serializable{
    
    private int usbValue;

    private boolean usbAllowed;

    private String[] usbList;

    private LocalDateTime creationDate;

    private LocalDateTime lastModificationDate;

    private String description;
}

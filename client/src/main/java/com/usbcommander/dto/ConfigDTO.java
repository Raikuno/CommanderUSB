package com.usbcommander.dto;

public class ConfigDTO{
    private Boolean allow;
    private Long frecuency;
    private Long allowedTime;

    public Boolean isAllow() {
        return allow;
    }
    public void setAllow(Boolean allow) {
        this.allow = allow;
    }
    public Long getFrecuency() {
        return frecuency;
    }
    public void setFrecuency(Long frecuency) {
        this.frecuency = frecuency;
    }
    public Long getAllowedTime() {
        return allowedTime;
    }
    public void setAllowedTime(Long allowedTime) {
        this.allowedTime = allowedTime;
    }

    
}

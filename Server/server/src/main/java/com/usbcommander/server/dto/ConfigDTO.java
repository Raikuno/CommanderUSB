package com.usbcommander.server.dto;
/**
 * Clase usada únicamanete como DTO (Data transfer object) para ser enviada y recibida en comunicaciones entre el cliente y el servidor en relación a la configuración de la aplicación cliente
 */
public class ConfigDTO{
    private Boolean allow;
    private Long frecuency;
    private Long allowedTime;

    public ConfigDTO(){}

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

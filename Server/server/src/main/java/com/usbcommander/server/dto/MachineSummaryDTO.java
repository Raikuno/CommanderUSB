package com.usbcommander.server.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class MachineSummaryDTO {
    private UUID id;
    private String name;
    private String ip;
    private Boolean disable;
    private Long logFrecuency;
    private LocalDateTime registeredDate;
    private String description;
    private boolean connected;
    private Integer topUnrevisedLogCode;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Boolean getDisable() { return disable; }
    public void setDisable(Boolean disable) { this.disable = disable; }

    public Long getLogFrecuency() { return logFrecuency; }
    public void setLogFrecuency(Long logFrecuency) { this.logFrecuency = logFrecuency; }

    public LocalDateTime getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDateTime registeredDate) { this.registeredDate = registeredDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public Integer getTopUnrevisedLogCode() { return topUnrevisedLogCode; }
    public void setTopUnrevisedLogCode(Integer topUnrevisedLogCode) { this.topUnrevisedLogCode = topUnrevisedLogCode; }
}

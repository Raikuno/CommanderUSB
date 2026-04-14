package com.usbcommander.server.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="machine")
public class Machine {
    @Id
    @GeneratedValue
    @Column(columnDefinition="BINARY(16)", updatable=false)
    private UUID id;

    @Column(nullable=false, unique=true)
    private String name;

    @Column(nullable=false, unique=true, name="reg_dt")
    private LocalDateTime registeredDate;

    private String description;

    @Column(nullable=false, columnDefinition="boolean default true")
    private Boolean enable;

    @Column(nullable=false, columnDefinition="unsigned int")
    private Integer logFrecuency;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getLogFrecuency() {
        return logFrecuency;
    }

    public void setLogFrecuency(Integer logFrecuency) {
        this.logFrecuency = logFrecuency;
    }

    
}

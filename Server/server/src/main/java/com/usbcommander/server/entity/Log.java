package com.usbcommander.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="log")
public class Log {
    @Id
    @GeneratedValue
    @Column(columnDefinition="BIGINT", updatable=false)
    private Integer id;

    @ManyToOne(targetEntity=Machine.class)
    @Column(nullable=false, name="machine_id")
    private Machine machine;

    @Column(nullable=false, name="received_date")
    private LocalDateTime recievedDate;

    @Column(nullable=false, name="usb_value")
    private Integer usbValue;

    @Column(nullable=false, columnDefinition="boolean", name="usb_allowed")
    private Boolean usbAllowed;

    @Column(nullable=false, name="log_code")
    private Integer logCode;
    
    @Column(name="expected_date", nullable=false)
    private LocalDateTime expectedDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public LocalDateTime getRecievedDate() {
        return recievedDate;
    }

    public void setRecievedDate(LocalDateTime recievedDate) {
        this.recievedDate = recievedDate;
    }

    public Integer getUsbValue() {
        return usbValue;
    }

    public void setUsbValue(Integer usbValue) {
        this.usbValue = usbValue;
    }

    public Boolean getUsbAllowed() {
        return usbAllowed;
    }

    public void setUsbAllowed(Boolean usbAllowed) {
        this.usbAllowed = usbAllowed;
    }

    public Integer getLogCode() {
        return logCode;
    }

    public void setLogCode(Integer logCode) {
        this.logCode = logCode;
    }

    public LocalDateTime getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(LocalDateTime expectedDate) {
        this.expectedDate = expectedDate;
    }

    
}

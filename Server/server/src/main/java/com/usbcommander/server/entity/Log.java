package com.usbcommander.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="log",
    uniqueConstraints = @UniqueConstraint(columnNames = {"creation_date", "machine_id"})
)
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="BIGINT", updatable=false)
    private Long id;

    @ManyToOne(targetEntity=Machine.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Column(nullable=false, name="receive_date")
    private LocalDateTime recievedDate;

    @Column(nullable=false, name="usb_value", columnDefinition = "TINYINT")
    private Integer usbValue;

    @Column(nullable=false, columnDefinition="boolean", name="usb_allowed")
    private Boolean usbAllowed;

    @Column(nullable=false, name="log_code")
    private Integer logCode;
    
    @Column(name="creation_date", nullable=false)
    private LocalDateTime creationDate;

    @Column(name = "needs_rev", nullable = false)
    private Boolean needsRevission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getNeedsRevission() {
        return needsRevission;
    }

    public void setNeedsRevission(Boolean needsRevission) {
        this.needsRevission = needsRevission;
    }

    
}

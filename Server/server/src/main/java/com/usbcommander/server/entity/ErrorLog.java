package com.usbcommander.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="error_log")
public class ErrorLog {
    @Id
    @GeneratedValue
    @Column(columnDefinition="BIGINT", updatable=false)
    private Integer id;

    @ManyToOne(targetEntity=Machine.class)
    @Column(nullable=false, name="machine_id")
    private Machine machine;

    @Column(nullable=false, name="received_date")
    private LocalDateTime recievedDate;

    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}

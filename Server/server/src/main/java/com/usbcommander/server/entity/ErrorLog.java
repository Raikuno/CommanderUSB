package com.usbcommander.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="error_log",
    uniqueConstraints = @UniqueConstraint(columnNames = {"creation_date", "machine_id"})
)
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="BIGINT", updatable=false)
    private Long id;

    @ManyToOne(targetEntity=Machine.class)
    private Machine machine;

    @Column(nullable=false, name="receive_date")
    private LocalDateTime recievedDate;

    @Column(nullable=false, name="creation_date")
    private LocalDateTime creationDate;

    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}

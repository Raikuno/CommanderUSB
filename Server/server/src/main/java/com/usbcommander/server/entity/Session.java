package com.usbcommander.server.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
/**
 * Clase representativa de las filas de la tabla 'sessions' de la base de datos
 */
public class Session {
    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition="BINARY(16)", updatable=false)
    private UUID id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private String selector;

    @Column(nullable = false, columnDefinition = "CHAR(64)")
    private String token;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean blacklisted;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(Boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    
}

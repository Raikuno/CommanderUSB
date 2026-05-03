package com.usbcommander.server.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition="BINARY(16)", updatable=false)
    private UUID id;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String name;

    @JsonIgnore
    @Column(nullable=false)
    private String password;

    @Column(nullable=false, columnDefinition="boolean default false")
    private Boolean disable;

    @ManyToOne(targetEntity=Role.class)
    @JoinColumn(name = "role_id", nullable = true)
    private Role role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    
}

package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.Permission;
import com.usbcommander.server.repository.PermissionRepository;

@Service
/**
 * Implementación de IPermissionService
 */
public class PermissionService implements IPermissionService{
    @Autowired
    /**
     * Repositorio vinculado al servicio
     */
    private PermissionRepository repository;

    @Override
    public Optional<Permission> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Permission> getByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<Permission> getAll() {
        return repository.findAll();
    }
    
}

package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.Role;
import com.usbcommander.server.repository.RoleRepository;

@Service
public class RoleService implements IRoleService{

    @Autowired
    private RoleRepository repository;

    @Override
    public Optional<Role> getByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<Role> getById(UUID uuid) {
        return repository.findById(uuid);
    }

    @Override
    public List<Role> getAll() {
        return repository.findAll();
    }

    @Override
    public void save(Role role) {
        repository.save(role);
    }
    
}

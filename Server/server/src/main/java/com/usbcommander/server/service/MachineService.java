package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.repository.MachineRepository;

@Service
public class MachineService implements IMachineService{
    @Autowired
    private MachineRepository repository;

    @Override
    public Optional<Machine> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Machine> getByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<Machine> getByEnable(Boolean enable) {
        return repository.findByEnable(enable);
    }

    @Override
    public List<Machine> getByRegisteredDate(LocalDateTime registeredDate) {
        return repository.findByRegisteredDate(registeredDate);
    }

    @Override
    public List<Machine> getAll() {
        return repository.findAll();
    }
    
}

package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.Machine;

public interface IMachineService {
    public List<Machine> getAll();
    
    public Optional<Machine> getById(UUID id);

    public Optional<Machine> getByName(String name);

    public Optional<Machine> getByIp(String ip);

    public List<Machine> getByEnable(Boolean enable);

    public List<Machine> getByRegisteredDate(LocalDateTime registeredDate);

    public void save(Machine machine);
}

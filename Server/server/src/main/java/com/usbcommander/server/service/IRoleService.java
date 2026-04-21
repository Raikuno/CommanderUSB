package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.Role;

public interface IRoleService {
    public List<Role> getAll();
    public Optional<Role> getByName(String name);
    public Optional<Role> getById(UUID uuid);
    public void save(Role role);
}

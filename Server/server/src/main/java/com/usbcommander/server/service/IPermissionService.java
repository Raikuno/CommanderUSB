package com.usbcommander.server.service;

import java.security.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPermissionService {
    public List<Permission> getAll();
    public Optional<Permission> getById(UUID id);
    public Optional<Permission> getByName(String name);
}

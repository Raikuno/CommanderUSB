package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.Role;
import com.usbcommander.server.entity.User;

public interface IUserService {
    public List<User> getAll();
    public Optional<User> getByName(String name);
    public Optional<User> getByEmail(String email);
    public Optional<User> getById(UUID id);
    public List<User> getByRole(Role role);
    public void update(User user);
    public void updatePassword(User user, String newPassword);
    public void create(User user);
}

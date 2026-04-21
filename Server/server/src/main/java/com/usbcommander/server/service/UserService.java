package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.Role;
import com.usbcommander.server.entity.User;
import com.usbcommander.server.repository.UserRepository;

@Service
public class UserService implements IUserService{
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<User> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> getByRoleId(Role roleId) {
        return repository.findByRoleId(roleId);
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }
    
}

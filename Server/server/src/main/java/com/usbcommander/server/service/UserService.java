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
    public List<User> getByRole(Role role) {
        return repository.findByRole(role);
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public void update(User user) {
        user.setPassword(repository.findById(user.getId()).get().getPassword());
        repository.save(user);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    @Override
    public void create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return repository.findByEmail(email);
    }
    
}

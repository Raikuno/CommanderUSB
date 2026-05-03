package com.usbcommander.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.User;

@Service
/**
 * Implementación de IFirstStartService
 */
public class FirstStartService implements IFirstStartService {

    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;

    @Override
    public boolean adminAccountCreated() {
        return !userService.getAll().isEmpty();
    }

    @Override
    public void createAdminAccount(String email, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setRole(roleService.getByName("ADMIN").get());
        user.setEmail(email);
        user.setDisable(false);
        
        userService.create(user);
    }
    
}

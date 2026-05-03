package com.usbcommander.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.usbcommander.server.entity.User;
import com.usbcommander.server.repository.UserRepository;

@Service
/**
 * Implementación de la clase de Spring UserDetailsService para construir el servicio encargado de buscar los datos del usuario registrado
 */
public class CommanderUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> 
                new UsernameNotFoundException("User not found"));
        return new CommanderUserDetails(user);
    }
    
}

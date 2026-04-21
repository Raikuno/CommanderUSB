package com.usbcommander.server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.User;

import java.util.Optional;
import java.util.List;
import com.usbcommander.server.entity.Role;


@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    
    Optional<User> findByName(String name);
    Optional<User> findById(UUID id);
    List<User> findByRoleId(Role roleId);
    List<User> findByDisable(Boolean disable);
    Optional<User> findByEmail(String email);

}

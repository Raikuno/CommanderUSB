package com.usbcommander.server.repository;

import java.security.Permission;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID>{
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByName(String name);
}

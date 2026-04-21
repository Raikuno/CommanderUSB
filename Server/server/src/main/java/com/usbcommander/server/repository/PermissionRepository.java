package com.usbcommander.server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID>{
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByName(String name);
}

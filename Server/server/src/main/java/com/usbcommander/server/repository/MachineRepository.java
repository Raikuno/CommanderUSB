package com.usbcommander.server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usbcommander.server.entity.Machine;
import java.util.List;
import java.time.LocalDateTime;



public interface MachineRepository extends JpaRepository<Machine, UUID>{
    Optional<Machine> findById(UUID id);

    Optional<Machine> findByName(String name);
    Optional<Machine> findByIp(String ip);

    List<Machine> findByDisable(Boolean disable);

    List<Machine> findByRegisteredDate(LocalDateTime registeredDate);

}
